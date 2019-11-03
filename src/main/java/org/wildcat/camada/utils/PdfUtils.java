package org.wildcat.camada.utils;

import javafx.collections.ObservableList;
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

@Slf4j
public class PdfUtils {

    public static void main(String... args) {
        export(null, "date.pdf");
    }

    public static <T> Boolean export(ObservableList<T> items, String fileName) {
        boolean result = false;
        try {
            ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();

            templateResolver.setPrefix("/pdf/templates/");
            templateResolver.setSuffix(".html");
            templateResolver.setTemplateMode("HTML");

            TemplateEngine templateEngine = new TemplateEngine();
            templateEngine.setTemplateResolver(templateResolver);

            Context context = new Context();
            context.setVariable("headingTitle", "Listado de usuarios");
            context.setVariable("headingDate", LocalDateTime.now());

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

    /*
    class CustomTextUserAgent extends ITextUserAgent {
        public CustomTextUserAgent(ITextOutputDevice outputDevice) {
            super(outputDevice);
        }

        @Override
        protected InputStream resolveAndOpenStream(String uri) {
            if (uri.startsWith("file:")) {
                String path = uri.substring("file:".length());

                InputStream is = getClass().getClassLoader().getResourceAsStream(String.format("reports%s", path));
                if (is != null) {
                    return is;
                }
            }
            return super.resolveAndOpenStream(uri);
        }
    }
     */
}
