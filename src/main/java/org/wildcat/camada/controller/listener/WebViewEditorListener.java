package org.wildcat.camada.controller.listener;

import com.sun.javafx.webkit.Accessor;
import com.sun.webkit.WebPage;
import javafx.beans.value.ChangeListener;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebView;

public class WebViewEditorListener {

    private final ChangeListener<String> listener;

    private final WebPage webPage;

    private String htmlRef, innerText;

    public WebViewEditorListener(final WebView editor, ChangeListener<String> listener) {
        this.listener = listener;
        webPage = Accessor.getPageFor(editor.getEngine());
        editor.addEventFilter(KeyEvent.KEY_TYPED, e -> onKeyTyped(webPage.getHtml(webPage.getMainFrame())));
    }

    private void onKeyTyped(final String html) {
        boolean isEqual = htmlRef != null ? htmlRef.length() == html.length() : html == null;
        if (!isEqual) {
            String text = webPage.getInnerText(webPage.getMainFrame());
            listener.changed(null, innerText, text);
            innerText = text;
            htmlRef = html;
        }
    }

}