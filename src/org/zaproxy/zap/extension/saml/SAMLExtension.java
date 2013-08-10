package org.zaproxy.zap.extension.saml;

import org.parosproxy.paros.Constant;
import org.parosproxy.paros.control.Control;
import org.parosproxy.paros.extension.ExtensionAdaptor;
import org.parosproxy.paros.extension.ExtensionHook;
import org.parosproxy.paros.extension.ExtensionPopupMenuItem;
import org.parosproxy.paros.extension.SessionChangedListener;
import org.parosproxy.paros.extension.manualrequest.ManualRequestEditorDialog;
import org.parosproxy.paros.extension.manualrequest.http.impl.ManualHttpRequestEditorDialog;
import org.parosproxy.paros.model.HistoryReference;
import org.parosproxy.paros.model.Session;
import org.zaproxy.zap.extension.ExtensionPopupMenu;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;

public class SAMLExtension extends ExtensionAdaptor {

    @Override
    public URL getURL() {
        try {
            return new URL(Constant.ZAP_HOMEPAGE);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    @Override
    public String getAuthor() {
        return Constant.ZAP_TEAM;
    }

    @Override
    public void hook(ExtensionHook extensionHook) {
        super.hook(extensionHook);

        if (getView() != null) {

            final SAMLProxyListener proxyListener = new SAMLProxyListener();
            extensionHook.addProxyListener(proxyListener);

            ExtensionPopupMenu samlMenu = new ExtensionPopupMenu("SAML Actions");
            ExtensionPopupMenuItem samlResendMenuItem = new SAMLResendMenuItem("Resend...");

            samlMenu.add(samlResendMenuItem);
            extensionHook.getHookMenu().addPopupMenuItem(samlMenu);

            JMenuItem samlActiveEditorMenu = new JMenuItem("SAML Request Editor");
            samlActiveEditorMenu.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    AutoChangerSettingUI settingUI = new AutoChangerSettingUI(proxyListener);
                    settingUI.setVisible(true);
                }
            });

            extensionHook.getHookMenu().addToolsMenuItem(samlActiveEditorMenu);

        }

    }
}
