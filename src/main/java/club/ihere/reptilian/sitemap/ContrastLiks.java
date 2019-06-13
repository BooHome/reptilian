package club.ihere.reptilian.sitemap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ContrastLiks {

    public static void main(String[] args) {
        File file_v1=new File("D:\\seo\\1.txt");
        try {
            Set<String> links_v1 = readFile(file_v1);
            System.out.println("links_v1:");
            for (String item : links_v1) {
                System.out.println(item);
            }
            System.out.println(links_v1.size());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static Set<String> readFile(File fin) throws IOException {
        Set<String> linkSet=new HashSet<>();
        BufferedReader br = new BufferedReader(new FileReader(fin));
        String line = null;
        while ((line = br.readLine()) != null) {
            linkSet.add(line);
        }
        br.close();
        return linkSet;
    }
}
