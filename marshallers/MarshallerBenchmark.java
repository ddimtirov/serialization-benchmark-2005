package serialization.marshallers;

import static java.lang.System.currentTimeMillis;
import java.util.List;
import java.io.IOException;

import serialization.domain.Human;
import serialization.domain.Factory;

/**
 * @author ddimitro
 * Date: Sep 25, 2005 3:48:47 AM
 */
public class MarshallerBenchmark {

    public static void main(String[] args) throws IOException {
        Serializer<List<Human>> xs = new Serializer.XStreamDOM<List<Human>>(null, "XStream", false, false, false);
        Serializer<List<Human>> xsa = new Serializer.XStreamDOM<List<Human>>(null, "XStream+Aliases", false, true, false);
        Serializer<List<Human>> xsi = new Serializer.XStreamDOM<List<Human>>(null, "XStream+id", true, false, false);
        Serializer<List<Human>> xsp = new Serializer.XStreamDOM<List<Human>>(null, "XStream+pretty", false, false, true);
        Serializer<List<Human>> xspp = new Serializer.XStreamXPP3<List<Human>>(null, "XStream", false, false, false);
        Serializer<List<Human>> xsppa = new Serializer.XStreamXPP3<List<Human>>(null, "XStream+XPP3+Aliases", false, true, false);
        Serializer<List<Human>> xsppi = new Serializer.XStreamXPP3<List<Human>>(null, "XStream+XPP3+id", true, false, false);
        Serializer<List<Human>> xsppp = new Serializer.XStreamXPP3<List<Human>>(null, "XStream+XPP3+pretty", false, false, true);
        Serializer<List<Human>> hessian = new Serializer.Hessian<List<Human>>(null);
        Serializer<List<Human>> compactId = new Serializer.CompactXmlId(null);
        Serializer<List<Human>> compactXP = new Serializer.CompactXmlXPath(null);
        benchmark(0, 10, 1, compactId, compactXP, xs, xsa, xsi, xsp, hessian);
//        benchmark(10, 100, 10, compactId, compactXP, xs, xsa, xsi, xsp, hessian);
//        benchmark(100, 1000, 100, compactId, compactXP, xs, xsa, xsi, xsp, hessian);
//        benchmark(1000, 10000, 1000, compactId, compactXP, xs, xsa, xsi, xsp, hessian);

        List<Human> graph = Factory.createGraph(15000);

        benchmarkTime(hessian, graph); // fails at 75,000
//        benchmarkTime(xspp, graph); // fails at 22,000
//        benchmarkTime(xsppi, graph); // fails at 22,000
//        benchmarkTime(xsppa, graph); // fails at 34,000
//        benchmarkTime(xsppp, graph); // fails at 18,000
        benchmarkTime(xs, graph); // fails at 22,000
        benchmarkTime(xsi, graph); // fails at 22,000
        benchmarkTime(xsa, graph); // fails at 34,000
        benchmarkTime(xsp, graph); // fails at 18,000
    }

    private static void benchmarkTime(Serializer<List<Human>> ser, List<Human> graph) throws IOException {
        ser.setTarget(graph);
        long start = currentTimeMillis();
        ser.asByteArray();
        System.out.printf("%16s %d entries = %4dms\n", ser, graph.size(), (currentTimeMillis() - start));
    }

    private static void benchmark(int start, int end, int step, Serializer<List<Human>>... serializers) throws IOException {
        System.out.printf("-- Tab separated table [%d..%d] step=%d --\n", start, end, step);
        System.out.print("Obj#\t");
        for (Serializer<List<Human>> ser : serializers) {
            System.out.printf("%s\t", ser);
        }
        System.out.print("Obj#\t");
        for (Serializer<List<Human>> ser : serializers) {
            System.out.printf("%s\t", new Serializer.Zip(ser));
        }
        System.out.println();

        for (int i=start; i<=end; i+=step){
            List<Human> graph = Factory.createGraph(i);

            System.out.print(i + "\t");
            for (Serializer<List<Human>> ser : serializers) {
                ser.setTarget(graph);
                System.out.printf("%6d\t", ser.size());
            }

            System.out.print(i + "\t");
            for (Serializer<List<Human>> ser : serializers) {
                System.out.printf("%6d\t", new Serializer.Zip(ser).size());
            }
            System.out.println();
        }
        System.out.println("---");

    }

}
