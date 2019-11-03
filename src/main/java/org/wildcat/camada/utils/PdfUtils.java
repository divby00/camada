package org.wildcat.camada.utils;

import javafx.scene.control.TableView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.pdf.ITextUserAgent;

import java.io.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class PdfUtils {

    public static void main(String... args) {
        export(null, "Listado de usuarios", "date.pdf");
    }

    public static <T> Boolean export(TableView<T> table, String title, String fileName) {
        boolean result = false;
        try {
            ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();

            templateResolver.setPrefix("/pdf/templates/");
            templateResolver.setSuffix(".html");
            templateResolver.setTemplateMode("HTML");

            TemplateEngine templateEngine = new TemplateEngine();
            templateEngine.setTemplateResolver(templateResolver);
            Context context = new Context();
            context.setVariable("headingTitle", title);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            context.setVariable("headingDate", formatter.format(LocalDateTime.now()));
            context.setVariable("tableHeaders", ReportUtils.getVisibleColumns(table));
            context.setVariable("tableContent", ReportUtils.getRecordsMap(table));

            String html = templateEngine.process("index", context);
            OutputStream outputStream = new FileOutputStream(fileName);
            ITextRenderer renderer = new ITextRenderer(300, 40);
            renderer.getSharedContext().setUserAgentCallback(new UserAgentCallback(renderer.getOutputDevice(), renderer.getSharedContext()));
            FileUtils.write(new File("out.html"), html);
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(outputStream);
            renderer.finishPDF();
            outputStream.close();
            result = true;
        } catch (Exception ignored) {
        }
        return result;
    }


    private static class UserAgentCallback extends ITextUserAgent {
        public UserAgentCallback(ITextOutputDevice outputDevice, SharedContext sharedContext) {
            super(outputDevice);
            setSharedContext(sharedContext);
        }

        @Override
        public String resolveURI(String uri) {
            return uri;
        }

        @Override
        protected InputStream resolveAndOpenStream(String uri) {
            java.io.InputStream is = null;
            URL url = null;
            try {
                url = new ClassPathResource("/pdf/" + uri).getURL();
            } catch (IOException e) {
                log.error("bad URL given: " + uri, e);
            }
            if (url == null) {
                log.error("Didn't find resource [" + uri + "].");
                return null;
            }
            try {
                is = url.openStream();
            } catch (java.net.MalformedURLException e) {
                log.error("bad URL given: " + uri, e);
            } catch (java.io.FileNotFoundException e) {
                log.error("item at URI " + uri + " not found");
            } catch (java.io.IOException e) {
                log.error("IO problem for " + uri, e);
            }
            return is;
        }
    }
}
