package com.ruijie.cpu.utils.file;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

public class XmlBuilder {

    /**
     * <p>Description: 将XML转为指定的object 不太好用</p>
     * <p>Create Time: 2019/7/17 </p>
     * @author zhengchengbin
     * @param 
     */
    public static Object xmlStrToOject(Class<?> clazz, String xmlStr) throws Exception {
        Object xmlObject = null;
        Reader reader = null;
        JAXBContext context = JAXBContext.newInstance(clazz);
        // XML 转为对象的接口
        Unmarshaller unmarshaller = context.createUnmarshaller();
        reader = new StringReader(xmlStr);
        //以文件流的方式传入这个string
        xmlObject = unmarshaller.unmarshal(reader);
        if (null != reader) {
            reader.close();
        }
        return xmlObject;
    }

    /**
     * <p>Description: xml 过于复杂时，通过只修改某些tag的值来更新xml ；默认只能修改第一个标签下的内容</p>
     * <p>Create Time: 2019/7/17 </p>
     * @author zhengchengbin
     * @param tags 多级标签 用##分割
     * @param changeInfo 需要修改的内容
     * @param xmlInput xml文件输入流
     */
    public static String xmlChangeInfo(String tags, String changeInfo, InputStream xmlInput){
        StringWriter writer = new StringWriter();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlInput);
            //通过文档对象获得该文档对象的根节点
            Element root = doc.getDocumentElement();
            String[] tagAry = tags.split("##");
            NodeList nodeList = root.getElementsByTagName(tagAry[0]);
            for (int i=1;i<tagAry.length;i++){
                Element element = (Element) nodeList.item(0);   //默认第一个
                nodeList = element.getElementsByTagName(tagAry[i]);
            }
            nodeList.item(0).setTextContent(changeInfo);


            //注意：XML文件是被加载到内存中 修改也是在内存中 ==》因此需要将内存中的数据同步到磁盘中

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            Source source = new DOMSource(doc);
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);

        }catch(Exception e){
            e.printStackTrace();
        }
        return writer.toString();
    }


}
