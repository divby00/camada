package org.wildcat.camada.utils;

import javafx.collections.ObservableList;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.wildcat.camada.entity.CamadaUser;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.FileOutputStream;
import java.io.OutputStream;

public class PdfUtils {

    public static void main(String... args) {
        export(null, "data.pdf");
    }

    public static Boolean export(ObservableList<CamadaUser> items, String fileName) {
        boolean result = false;
        try {
            ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();

            templateResolver.setPrefix("/templates/");
            templateResolver.setSuffix(".html");
            templateResolver.setTemplateMode("HTML");

            TemplateEngine templateEngine = new TemplateEngine();
            templateEngine.setTemplateResolver(templateResolver);

            Context context = new Context();
            context.setVariable("name", "Laura");

            String html = templateEngine.process("test", context);
            OutputStream outputStream = new FileOutputStream(fileName);
            ITextRenderer renderer = new ITextRenderer();
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
}
