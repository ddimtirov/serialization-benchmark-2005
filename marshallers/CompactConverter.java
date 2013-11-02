package serialization.marshallers;

import static serialization.domain.Factory.createGraph;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.XStream;

import java.beans.Introspector;
import java.beans.IntrospectionException;
import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Collection;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * @author ddimitro
 *         Date: Oct 3, 2005 1:42:50 AM
 */
public class CompactConverter implements Converter {
    public boolean canConvert(Class type) {
        return !type.isPrimitive() && !Collection.class.isAssignableFrom(type);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        try {
            BeanInfo bi = Introspector.getBeanInfo(source.getClass());
            PropertyDescriptor[] props = bi.getPropertyDescriptors();
            for (PropertyDescriptor prop : props) {
                if (prop.getName().equals("class")) continue;
                if (prop.getName().equals("content")) continue;

                Method readMethod = prop.getReadMethod();
                if (readMethod ==null) continue;

                Object value = readMethod.invoke(source);
                if (value==null) continue;

                boolean primitive =
                        value instanceof String ||
                        value instanceof Number ||
                        value instanceof Boolean ||
                        value instanceof Date ||
                        value.getClass().isEnum();

                if (primitive) {
                    writer.addAttribute(prop.getName(), String.valueOf(value));
                } else {
                    writer.startNode(prop.getName());
                    context.convertAnother(value);
                    writer.endNode();
                }
            }
        } catch (IntrospectionException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        throw new UnsupportedOperationException();
    }

    public static void main(String[] args) throws IOException {
        XStream xs = new XStream();
        xs.registerConverter(new CompactConverter(), Integer.MAX_VALUE);
        ObjectOutputStream oos = xs.createObjectOutputStream(new OutputStreamWriter(System.out));
        oos.writeObject(createGraph(2));
        oos.close();
    }
}
