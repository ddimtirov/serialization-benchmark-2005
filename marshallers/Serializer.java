package serialization.marshallers;

import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.CompactWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.XStream;

import com.caucho.hessian.io.HessianSerializerOutput;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.io.ObjectOutputStream;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;
import java.util.List;

import serialization.domain.Human;
import serialization.domain.Animal;
import serialization.domain.Dog;

/**
 * @author ddimitro
 * Date: Sep 25, 2005 7:38:56 PM
 */
public abstract class Serializer<T> {
    protected T o;
    protected Serializer(T o) { this.o = o; }
    public void setTarget(T o) { this.o = o; }
    public int size() throws IOException { return asByteArray().length; }

    public abstract byte[] asByteArray() throws IOException;

    private static abstract class ValueSerializer<T> extends Serializer<T> {
        private final String name;
        protected ValueSerializer(T o, String name) {
            super(o);
            this.name = name;
            this.o = o;
        }
        public String toString() { return name; }
    }

    public static final class Zip extends Serializer<Serializer> {
        public Zip(Serializer o) { super(o); }
        public String toString() { return String.format("Zip(%s)", o); }
        public byte[] asByteArray() throws IOException {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ZipOutputStream z = new ZipOutputStream(out);
            z.setLevel(9);
            z.putNextEntry(new ZipEntry("data"));
            z.write(o.asByteArray());
            z.closeEntry();
            z.close();
            return out.toByteArray();
        }
    }

    public static final class Hessian<T> extends ValueSerializer<T> {
        protected Hessian(T o) { super(o, "Hessian"); }
        public byte[] asByteArray() throws IOException {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            HessianSerializerOutput hsout = new HessianSerializerOutput(out);
            hsout.writeObject(o);
            out.close();
            return out.toByteArray();
        }
    }

    public static final class CompactXmlId extends ValueSerializer<List<Human>> {
        protected CompactXmlId(List<Human> o) { super(o, "CompactXmlId"); }
        public byte[] asByteArray() {
            StringBuffer sb = new StringBuffer();
            int id = 0;
            for (Human human : o) {
                Dog dog = (Dog) human.getPet();
                String entry= String.format(
                        "<human id=\"%d\" name=\"%s\" age=\"%d\">" +
                          "<pet id=\"%d\" breed=\"%s\" name=\"%s\"/>" +
                          "<spouse name=\"%s\" age=\"%d\">" +
                            "<pet id=\"%d\"/>" +
                            "<spouse id=\"%d\"/>" +
                          "</spouse>" +
                        "</human>",
                        id++, human.getName(), human.getAge(),
                        id++, dog.getBreed(), dog.getName(),
                        human.getSpouse().getName(), human.getSpouse().getAge(), id-2, id-1

                );
                sb.append(entry);
            }
            String xml = "<object-stream>" + sb.toString() + "</object-stream>";
            return xml.getBytes();
        }
    }

    public static final class CompactXmlXPath extends ValueSerializer<List<Human>> {
        protected CompactXmlXPath(List<Human> o) { super(o, "CompactXmlXPath"); }
        public byte[] asByteArray() {
            StringBuffer sb = new StringBuffer();
            int id = 0;
            for (Human human : o) {
                Dog dog = (Dog) human.getPet();
                String entry= String.format(
                        "<human name=\"%s\" age=\"%d\">" +
                          "<pet breed=\"%s\" name=\"%s\"/>" +
                          "<spouse name=\"%s\" age=\"%d\">" +
                            "<pet ref=\"../../pet\"/>" +
                            "<spouse ref=\"../..\"/>" +
                          "</spouse>" +
                        "</human>",
                        human.getName(), human.getAge(),
                        dog.getBreed(), dog.getName(),
                        human.getSpouse().getName(), human.getSpouse().getAge(), id-2, id-1

                );
                sb.append(entry);
            }
            String xml = "<object-stream>" + sb.toString() + "</object-stream>";
            return xml.getBytes();
        }
    }

    public static final class XStreamXPP3<T> extends XStreamDOM<T> {
        protected XStreamXPP3(T o, String name, boolean useIdRefs, boolean useAliases, boolean pretty) {
            super(o, name, useIdRefs, useAliases, pretty);
            xs = new XStream(new XppDriver());
        }
    }
    public static class XStreamDOM<T> extends ValueSerializer<T> {
        protected XStream xs = new XStream(new DomDriver());
        public boolean pretty = false;

        protected XStreamDOM(T o, String name, boolean useIdRefs, boolean useAliases, boolean pretty) {
            super(o, name);
            this.pretty=pretty;
            if (useAliases) {
                xs.alias("human", Human.class);
                xs.alias("animal", Animal.class, Dog.class);
            }
            if (useIdRefs) {
                xs.setMode(XStream.ID_REFERENCES);
            }
        }
        public byte[] asByteArray() throws IOException {
            StringWriter out = new StringWriter();
            HierarchicalStreamWriter hsw = pretty
                    ? new PrettyPrintWriter(out)
                    : new CompactWriter(out);
            ObjectOutputStream oos = xs.createObjectOutputStream(hsw);
            oos.writeObject(o);
            oos.close();
            return out.toString().getBytes();
        }
    }
}
