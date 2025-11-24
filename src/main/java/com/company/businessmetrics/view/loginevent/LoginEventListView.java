package com.company.businessmetrics.view.loginevent;

import com.company.businessmetrics.entity.LoginEvent;
import com.company.businessmetrics.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;


@Route(value = "login-events", layout = MainView.class)
@ViewController(id = "LoginEvent.list")
@ViewDescriptor(path = "login-event-list-view.xml")
@LookupComponent("loginEventsDataGrid")
@DialogMode(width = "64em")
public class LoginEventListView extends StandardListView<LoginEvent> {
}