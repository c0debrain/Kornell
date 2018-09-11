package kornell.gui.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class DisableInstitutionEvent extends GwtEvent<DisableInstitutionEventHandler> {
    public static final Type<DisableInstitutionEventHandler> TYPE = new Type<>();

    @Override
    public Type<DisableInstitutionEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(DisableInstitutionEventHandler handler) {
        handler.onServiceUnavailable();
    }

}
