package com.project.SSPS.service;

import com.project.SSPS.dto.PrinterDTO;
import com.project.SSPS.model.OrderPaper;
import com.project.SSPS.model.Printer;
import com.project.SSPS.model.PrintingLog;
import com.project.SSPS.repository.OrderPaperRepository;
import com.project.SSPS.repository.PrinterRepository;
import com.project.SSPS.repository.PrintingLogRepository;
import com.project.SSPS.response.*;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SpsoService implements ISpsoService {
    private final PrinterRepository printerRepository;
    private final PrintingLogRepository printingLogRepository;
    private final OrderPaperRepository orderPaperRepository;

    @Override
    public SpsoResponse findSpsoInfo(Long id) {
        return null;
    }

    @Override
    public PrinterResponse createPrinter(PrinterDTO printerDTO) {
        Printer printer = Printer.builder()
                .brand(printerDTO.getBrand())
                .buildingName(printerDTO.getBuildingName())
                .campusName(printerDTO.getCampusName())
                .description(printerDTO.getDescription())
                .model(printerDTO.getModel())
                .roomNum(printerDTO.getRoomNum())
                .enabled(printerDTO.isEnabled())
                .build();

        return PrinterResponse.fromPrinter(printerRepository.save(printer));
    }

    @Override
    public Page<PrinterResponse> getAllPrinters(PageRequest pageRequest) {
        Page<Printer> printers = printerRepository.findAll(pageRequest);
        return printers.map(PrinterResponse::fromPrinter);
    }

    @Override
    public PrinterResponse getPrinterById(Long printer_id) throws Exception {
        Printer printer = printerRepository.findById(printer_id).orElse(null);
        if (printer == null) {
            throw new Exception("Printer not found");
        }
        return PrinterResponse.fromPrinter(printer);
    }

    @Override
    public PrinterResponse updatePrinter(Long id, PrinterDTO entity) throws Exception {
        Printer printer = printerRepository.findById(id).orElse(null);
        if (printer == null) {
            throw new Exception("Printer not found");
        }
        printer.setBrand(entity.getBrand());
        printer.setBuildingName(entity.getBuildingName());
        printer.setCampusName(entity.getCampusName());
        printer.setDescription(entity.getDescription());
        printer.setModel(entity.getModel());
        printer.setRoomNum(entity.getRoomNum());
        printer.setEnabled(entity.isEnabled());
        return PrinterResponse.fromPrinter(printerRepository.save(printer));
    }

    @Override
    public RestResponse<Object> deletePrinter(Long id) throws Exception {
        Printer printer = printerRepository.findById(id).orElse(null);
        if (printer == null) {
            throw new Exception("Printer not found");
        }
        printerRepository.delete(printer);
        return new RestResponse<>(HttpServletResponse.SC_OK, null, "Delete printer have id " + id + " successfully!",
                null);
    }

    @Override
    public Page<PrintingLogResponse> getAllPrintingLogs(PageRequest pageRequest) {
        Page<PrintingLog> printingLogs = printingLogRepository.findAll(pageRequest);
        return printingLogs.map(PrintingLogResponse::fromPrintingLog);
    }

    @Override
    public PaymentLogsListResponse getAllPaymentLogs(PageRequest pageRequest) {
        Page<OrderPaper> orderPapers = orderPaperRepository.findAll(pageRequest);
        List<PaymentLogsResponse> paymentLogsResponses = orderPapers.stream()
                .map(orderPaper -> PaymentLogsResponse.fromOrderPaper(orderPaper, orderPaper.getOrder()))
                .collect(Collectors.toList());
        return new PaymentLogsListResponse(paymentLogsResponses, orderPapers.getTotalPages());
    }

    @Override
    public StatisticResponse getStatistic() {
        // long totalPrintingLogs = printingLogRepository.countPrintingLogs();
        // nOrderPaper = nPaymentLogs
        Long totalPurchasing = orderPaperRepository.countOrderPapers();
        Long totalPrinting = printingLogRepository.countPrintingLog();
        return new StatisticResponse(totalPrinting, totalPurchasing);
    }

}
