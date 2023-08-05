/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osmgraph3;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 *
 * @author viljinsky
 */
public class XMLGraph {

// // Функция добавления новой книги и записи результата в файл
    private static void addNewBook(Document document) throws TransformerFactoryConfigurationError, DOMException {
//        // Получаем корневой элемент
//        Node root = document.getDocumentElement();
// 
//        // Создаем новую книгу по элементам
//        // Сама книга <Book>
//        Element book = document.createElement("Book");
    }
//    

    public static void main(String[] args) {
        File file = new File("C:\\Users\\viljinsky\\Desktop", "test.osm");

        try {
            // Создается построитель документа
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            // Создается дерево DOM документа из файла
            Document document = documentBuilder.parse(file);//("BookCatalog.xml");

            // Вызываем метод для добавления новой книги
            addNewBook(document);

        } catch (ParserConfigurationException ex) {
            ex.printStackTrace(System.out);
        } catch (SAXException ex) {
            ex.printStackTrace(System.out);
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }

    }

}
