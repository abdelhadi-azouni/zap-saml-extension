package org.zaproxy.zap.extension.saml.ui;

import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.extension.saml.Attribute;
import org.zaproxy.zap.extension.saml.SAMLException;
import org.zaproxy.zap.extension.saml.SAMLMessage;
import org.zaproxy.zap.extension.saml.SAMLResender;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Map;

public class SamlManualEditor extends JFrame {

    private JTextPane msgPane;
    private JButton btnResend;
    private JButton btnReset;
    private SAMLMessage samlMessage;

    private JTextPane respHeadTextPane;
    private JTextPane respBodyTextPane;
    private JTabbedPane tabbedPane;
    private JScrollPane msgScrollPane;
    private JScrollPane attribScrollPane;



    /**
	 * Create the frame.
	 */
	public SamlManualEditor(final SAMLMessage samlMessage) {
        this.samlMessage = samlMessage;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(50, 50, 800, 700);
        JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		final JPanel reqPanel = new JPanel();
		tabbedPane.addTab("Request", null, reqPanel, null);
		reqPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		reqPanel.add(topPanel, BorderLayout.NORTH);
		
		JLabel lblNote = new JLabel("<html><p><b>Note :</b>This add-on would only run very basic test cases for SAML" +
                " implementations. <br/> Signed SAML assertions cannot be tampered with at this time <br/> because " +
                "the signings have not been" +
                " made available to ZAP</p></html>");
		topPanel.add(lblNote);

		JPanel centerPanel = new JPanel();
		reqPanel.add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new GridLayout(2, 1, 0, 10));
		
		msgScrollPane = new JScrollPane();
        centerPanel.add(msgScrollPane);

        attribScrollPane = new JScrollPane();
        centerPanel.add(attribScrollPane);
		
		JPanel bottomPanel = new JPanel();
		reqPanel.add(bottomPanel, BorderLayout.SOUTH);
		
        btnResend = new JButton("Resend");
        bottomPanel.add(btnResend);
		
        btnReset = new JButton("Reset");
        bottomPanel.add(btnReset);

        btnResend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    SAMLResender.resendMessage(SamlManualEditor.this.samlMessage.getChangedMessage());
                    updateResponse(SamlManualEditor.this.samlMessage.getChangedMessage());
                    btnResend.setEnabled(false);
                    btnReset.setEnabled(false);
                } catch (SAMLException e) {
                    JOptionPane.showMessageDialog(reqPanel, e.getMessage(), "Cannot resend request",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SamlManualEditor.this.samlMessage.resetChanges();
                updateFields();
            }
        });
		
		JPanel respPanel = new JPanel();
		tabbedPane.addTab("Response", null, respPanel, null);
		respPanel.setLayout(new GridLayout(2, 1, 0, 15));
		
		JScrollPane resHeadScrollPane = new JScrollPane();
		respPanel.add(resHeadScrollPane);
		
		respHeadTextPane = new JTextPane();
        resHeadScrollPane.setViewportView(respHeadTextPane);
		
		JScrollPane resBodyScrollPane = new JScrollPane();
		respPanel.add(resBodyScrollPane);
		

        respBodyTextPane = new JTextPane();
        resBodyScrollPane.setViewportView(respBodyTextPane);
        updateFields();
	}

    private void updateFields(){
        msgPane = new JTextPane();
        msgScrollPane.setViewportView(msgPane);
        msgPane.setText(samlMessage.getSamlMessageString());
        msgPane.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                samlMessage.setSamlMessageString(msgPane.getText());
                //todo check for validity
                updateFields();
            }
        });


        Map<String, Attribute> samlAttributes;
        samlAttributes = samlMessage.getAttributeMap();

        JPanel attributesPane = new JPanel();
        attribScrollPane.setViewportView(attributesPane);

        //1 row per attribute and 1 for relay state. if the total < 10 set it to 10 to have a better layout
        attributesPane.setLayout(new java.awt.GridLayout(Math.max(10, samlAttributes.size() + 1), 1, 5, 5));

        //text field to change relay state
        JSplitPane relayStatePane = new JSplitPane();
        JLabel lblRelayState = new JLabel();
        final JTextField txtRelayStateValue = new JTextField();

        relayStatePane.setDividerLocation(300);
        relayStatePane.setDividerSize(0);

        lblRelayState.setText("Relay State");
        relayStatePane.setLeftComponent(lblRelayState);

        txtRelayStateValue.setText(samlMessage.getRelayState());
        relayStatePane.setRightComponent(txtRelayStateValue);

        //update the saml message on attribute value changes
        txtRelayStateValue.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                samlMessage.setRelayState(txtRelayStateValue.getText());
            }
        });
        attributesPane.add(relayStatePane);

        //Text fields to change other attributes

        for (final Attribute attribute : samlAttributes.values()) {
            JSplitPane sPane = new JSplitPane();
            JLabel lbl = new JLabel();
            final JTextField txtValue = new JTextField();

            sPane.setDividerLocation(300);
            sPane.setDividerSize(0);

            lbl.setText(attribute.getViewName());
            sPane.setLeftComponent(lbl);

            txtValue.setText(attribute.getValue().toString());
            sPane.setRightComponent(txtValue);

            //update the saml message on attribute value changes
            txtValue.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                }

                @Override
                public void focusLost(FocusEvent e) {
                    samlMessage.changeAttributeValueTo(attribute.getName(),txtValue.getText());
                    updateFields();
                }
            });
            attributesPane.add(sPane);
        }
    }

    private void updateResponse(HttpMessage msg) {
        respBodyTextPane.setText(msg.getResponseBody().createCachedString("UTF-8"));
        respHeadTextPane.setText(msg.getResponseHeader().toString());
        tabbedPane.setSelectedIndex(1);
    }
}
