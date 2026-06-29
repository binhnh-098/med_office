package com.example.med_office.service.einvoice;

import com.example.med_office.entity.SalesOrder;

public interface EInvoiceService {
    void issueInvoice(SalesOrder salesOrder);
}
