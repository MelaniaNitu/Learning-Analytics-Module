package com.policat.LA.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

@Service
public class PdfGeneratorService {
    @Autowired
    private TemplateEngine templateEngine;
    public void createPdf(String templateName, Map map, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Assert.notNull(templateName, "The templateName can not be null");
        WebContext ctx = new WebContext(request, response, request.getServletContext());
        if (map != null) {
            Iterator itMap = map.entrySet().iterator();
            while (itMap.hasNext()) {
                Map.Entry pair = (Map.Entry) itMap.next();
                ctx.setVariable(pair.getKey().toString(), pair.getValue());
            }
        }

        String processedHtml = templateEngine.process(templateName, ctx);
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(processedHtml);
        renderer.layout();
        renderer.createPDF(response.getOutputStream(), false);
        renderer.finishPDF();
        System.out.println("PDF created successfully");
    }
}
