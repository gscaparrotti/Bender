package com.github.gscaparrotti.bender.springFilters;

import com.github.gscaparrotti.bender.controller.IMainController;
import com.github.gscaparrotti.bender.controller.MainController;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.swing.*;
import org.springframework.stereotype.Component;

@Component
public class UpdateBridge implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);
        boolean update = true;
        if (request instanceof HttpServletRequest) {
            final HttpServletRequest httpRequest = (HttpServletRequest) request;
            update = httpRequest.getMethod().equalsIgnoreCase("POST") || httpRequest.getMethod().equalsIgnoreCase("DELETE");
        }
        if (update) {
            final IMainController mainController = MainController.getInstance();
            SwingUtilities.invokeLater(() -> {
                if (mainController.getDialogController() != null) {
                    mainController.getDialogController().updateOrdersInView();
                    mainController.getDialogController().updateTableNameInView();
                }
                if (mainController.getMainViewController() != null) {
                    mainController.getMainViewController().refreshTablesInView();
                    mainController.getMainViewController().updateUnprocessedOrdersInView();
                    mainController.getMainViewController().updateTableNamesInView();
                }
            });
        }
    }

}