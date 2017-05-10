package kornell.gui.client.presentation.admin.common;

import com.github.gwtbootstrap.client.ui.Modal;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
public class GenericConfirmModalView extends Composite {
	interface MyUiBinder extends UiBinder<Widget, GenericConfirmModalView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	static
	Modal confirmModal;
	@UiField
    Label confirmText;
	@UiField
	Button btnOK;
	@UiField
	Button btnCancel;

	private Callback<Void, Void> callback;


	public GenericConfirmModalView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void showModal(String message, Callback<Void, Void> callback) {
		confirmText.setText(message);
		this.callback = callback;
		confirmModal.show();
	}
	
	@UiHandler("btnOK")
	void doOK(ClickEvent e) { 
		callback.onSuccess(null);
		confirmModal.hide();
	}
	

	@UiHandler("btnCancel")
	void doCancel(ClickEvent e) { 
		callback.onFailure(null);
		confirmModal.hide();
	}

}