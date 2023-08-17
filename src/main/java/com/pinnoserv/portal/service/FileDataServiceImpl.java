package com.pinnoserv.portal.service;

import com.pinnoserv.portal.custommodels.ApiResponse;
import com.pinnoserv.portal.custommodels.pythonmodels.PythonResponse;
import com.pinnoserv.portal.custommodels.responseutils.ResponseUtil;
import com.pinnoserv.portal.entity.FileData;
import com.pinnoserv.portal.entity.Image;
import com.pinnoserv.portal.entity.StatementReport;
import com.pinnoserv.portal.repositories.FileDataRepository;
import com.pinnoserv.portal.repositories.StatementReportRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.pinnoserv.portal.custommodels.responseutils.ResponseUtil.DIRECTORY;

@Service
@Slf4j
public class FileDataServiceImpl implements FileDataService{

    @Autowired
    private FileDataRepository fileDataRepository;
    @Autowired
    private StatementReportRepository statementReportRepository;
    private final WebClient webClient;
    ApiResponse fileApiResponse = new ApiResponse();
    public FileDataServiceImpl(WebClient webClient){
        this.webClient = webClient;

    }
    private final String FOLDER_PATH = "C:\\Users\\user\\Desktop\\statements\\";

    @Override
    public ApiResponse uploadImageToFileSystem(MultipartFile file) throws IOException {
        log.info("Begin the Upload service");
        String filePath = FOLDER_PATH+file.getOriginalFilename();
        log.info("My File Path {}", filePath);
        String customFilePath = DIRECTORY+file.getOriginalFilename();
        log.info("{}",customFilePath);

        FileData fileData = FileData.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .imagePath(filePath)
                .status(ResponseUtil.SUCCESSFUL_FILE_UPLOAD_STATUS)
                .build();
        fileDataRepository.save(fileData);
        file.transferTo(new File(filePath));

        PythonResponse payload = null;
        Long fileDataId = fileData.getId();
        if(!file.isEmpty()){

            log.info("My Id is, {}", fileDataId);

            FileData fileDataScoreEngine = fileDataRepository.findById(fileDataId).get();
            fileDataScoreEngine.setStatus(ResponseUtil.PROCESSING_SCORING_ENGINE_FILE_UPLOAD_STATUS);
            fileDataRepository.save(fileDataScoreEngine);

            payload = webClient.get()
                    .uri("http://127.0.0.1:8000/analysis/?url="+filePath)
                    .retrieve()
                    .bodyToMono(PythonResponse.class)
                    .block();
        }

        if (payload.paidIn == null && payload.paidOut == null){
            fileApiResponse.setResponseCode("01");
            fileApiResponse.setResponseDescription("Statement Analysis Not Successful");
            fileApiResponse.setEntity(payload);
            log.info("My Info Is : ", payload);

            FileData fileDataScoreEngine = fileDataRepository.findById(fileDataId).get();
            fileDataScoreEngine.setStatus(ResponseUtil.ERROR_FILE_UPLOAD_STATUS);
            fileDataRepository.save(fileDataScoreEngine);
            return fileApiResponse;
        }

            fileApiResponse.setResponseCode("00");
            fileApiResponse.setResponseDescription("Statement Analysis Successful");
            fileApiResponse.setEntity(payload);
            log.info("My Info Is : ", payload);
            StatementReport mpesaReport = StatementReport.builder()
                    .agentDeposit(payload.paidIn.agentDeposit)
                    .customersReceived(payload.paidIn.customesRecieved)
                    .paybillBanks(payload.paidIn.paybillBanks)
                    .fulizaReceived(payload.paidIn.fulizaRecieved)
                    .paybillBetting(payload.paidIn.paybillBeting)
                    .paybillLenders(payload.paidIn.paybillLenders)
                    .paybillOthers(payload.paidIn.paybillOthers)
                    .paidOutPaybillOthers(payload.paidIn.paybillOthers)
                    .paidOutCustomersSent(payload.paidOut.customersSent)
                    .paidOutAgentWithdraw(payload.paidOut.agentWithdraw)
                    .paidOutOthers(payload.paidOut.others)
                    .paidOutBuyGoods(payload.paidOut.buyGoods)
                    .paidOutBanks(payload.paidOut.banks)
                    .paidOutUtilities(payload.paidOut.utilities)
                    .paidOutFulizaPaid(payload.paidOut.fulizaPaid)
                    .paidOutOnlinePurchases(payload.paidOut.onlinePurchases)
                    .paidOutBetting(payload.paidOut.betting)
                    .paidOutMobileLenders(payload.paidOut.mobileLenders)
                    .build();
            statementReportRepository.save(mpesaReport);
            FileData fileDataScoreEngine = fileDataRepository.findById(fileDataId).get();
            fileDataScoreEngine.setStatus(ResponseUtil.SUCCESSFUL_IN_PROCESSING_SCORING_ENGINE_FILE_UPLOAD_STATUS);
            fileDataRepository.save(fileDataScoreEngine);

            return fileApiResponse;
    }

    @Override
    public byte[] downloadImageFromFileSystem(String fileName) throws IOException {
        Optional<FileData> fileData = fileDataRepository.findByName(fileName);
        String filePath = fileData.get().getImagePath();
        byte[] images = Files.readAllBytes(new File(filePath).toPath());
        return images;
    }

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        log.info("Begin the Upload service");

        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        String filePath = FOLDER_PATH+file.getOriginalFilename();
        log.info("My File Path {}", filePath);
        String customFilePath = DIRECTORY+file.getOriginalFilename();
        log.info("{}",customFilePath);

        FileData fileData = FileData.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .imagePath(filePath)
                .build();
        fileDataRepository.save(fileData);
        file.transferTo(new File(filePath));
        return file.getOriginalFilename();
    }
}