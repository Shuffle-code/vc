package tt.chat.vc.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import tt.chat.vc.entity.Observer;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateRatingTtw {

    private final ObserverService observerService;
    

//    @Scheduled(cron = "0****MON") // TODO: 03.03.2023 проверить работу метода по расписанию
    public void parseRatingWithTTW() throws DOMException, XPathExpressionException, ParserConfigurationException, IOException, SAXException {
        List<String> listIdTtw = observerService.getIdTtw();
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.parse("C:\\Users\\79130\\IdeaProjects\\tt_nsk\\storage\\xml\\observers.xml");
        XPath xpath = XPathFactory.newInstance().newXPath();
        for (int i = 0; i < listIdTtw.size(); i++) {
            int finalI = i;
            xpath.setXPathVariableResolver(new XPathVariableResolver() {
                @Override
                public Object resolveVariable(QName variableName) {
                    if (variableName.getLocalPart().equals("id"))
                        return listIdTtw.get(finalI);
                    else
                        return "";
                }
            });
            XPathExpression expr = xpath.compile("Observers/Observer[@id=$id]");
            NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
            for (int j = 0; j < nodes.getLength(); j++) {
                Node n = nodes.item(j);
                String textContent = n.getTextContent();
                String[] split = textContent.split("\n");
                Observer observerByRatingTtw = observerService.getObserverIdByIdTtw(listIdTtw.get(i));
                observerService.updateRatingTtw(observerByRatingTtw, new BigDecimal(split[8].replaceAll("\\s", "")));
            }
        }
    }

    @Scheduled(fixedDelay = 86400000) // fixedDelay = 86400000 каждые сутки, cron = " 0 * * * * MON" каждый понедельник
    public void parseRating(){
        List<String> listIdTtw = observerService.getIdTtw();
        for (int i = 0; i < listIdTtw.size(); i++) {
            String url = "http://r.ttw.ru/observers/?id=" + listIdTtw.get(i);
            try {
                org.jsoup.nodes.Document document = Jsoup.connect(url)
                        .userAgent("Chrome")
                        .timeout(500000)
                        .referrer("https://google.com")
                        .get();
                Elements ratingTtw = document.getElementsByClass("header-rating");
                for (Element el : ratingTtw) {
                    String text = el.ownText();
                    Observer observerByRatingTtw = observerService.getObserverIdByIdTtw(listIdTtw.get(i));
                    observerService.updateRatingTtw(observerByRatingTtw, new BigDecimal(text));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void parseRatingByClickingOnClient(String idTtw) {
        String url = "http://r.ttw.ru/observers/?id=" + idTtw;
        try {
            org.jsoup.nodes.Document document = Jsoup.connect(url)
                    .userAgent("Chrome")
                    .timeout(100000)
                    .referrer("https://google.com")
                    .get();
            Elements ratingTtw = document.getElementsByClass("header-rating");
            for (Element el : ratingTtw) {
                String text = el.ownText();
                Observer observerByRatingTtw = observerService.getObserverIdByIdTtw(idTtw);
                observerService.updateRatingTtw(observerByRatingTtw, new BigDecimal(text));
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
