package com.dong.myboard.controller;


import com.dong.myboard.domain.AttachFileDTO;
import lombok.extern.log4j.Log4j2;

import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
@Log4j2
public class UploadController {
    @GetMapping("/uploadAjax")
    public void uploadAjax(){
        log.info("upload ajax");
    }

    @PostMapping(value = "/uploadAjaxAction",produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<AttachFileDTO>> uploadAjaxPost(MultipartFile[] uploadFile){
        log.info("update ajax post.....");

        List<AttachFileDTO> list=new ArrayList<>();
        String uploadFolder="C://upload";
        String uploadFolderPath=getFolder();

        File uploadPath=new File(uploadFolder,uploadFolderPath);
        log.info("upload path: "+uploadPath);

        if(!uploadPath.exists()){
            uploadPath.mkdirs();
        }

        for(MultipartFile multipartFile:uploadFile){
            AttachFileDTO attachFileDTO=new AttachFileDTO();

            String uploadFileName=multipartFile.getOriginalFilename();

            uploadFileName=uploadFileName.substring(uploadFileName.lastIndexOf("\\")+1);
            log.info("only file name: "+uploadFileName);

            attachFileDTO.setFileName(uploadFileName);

            UUID uuid=UUID.randomUUID();
            uploadFileName=uuid.toString()+ "_"+ uploadFileName;

            try{
                File saveFile=new File(uploadPath,uploadFileName);
                multipartFile.transferTo(saveFile);//저장
                attachFileDTO.setUuid(uuid.toString());
                attachFileDTO.setUploadPath(uploadFolderPath);

                if(checkImageType(saveFile)){
                    attachFileDTO.setImage(true);
                    FileOutputStream thumbnail=new FileOutputStream(new File(uploadPath,"s_"+uploadFileName));
                    Thumbnailator.createThumbnail(new FileInputStream(new File(uploadPath, uploadFileName)),thumbnail,100,100);
                    thumbnail.close();
                }
                list.add(attachFileDTO);
            }catch (Exception e){
                log.error(e.getMessage());
            }
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/display")
    @ResponseBody
    public ResponseEntity<byte[]>getFile(String fileName) {
        File file = new File("c:\\upload\\" + fileName);
        ResponseEntity<byte[]> result = null;

        try {
            HttpHeaders headers = new HttpHeaders();

            headers.add("Content-Type", Files.probeContentType(file.toPath()));
            result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(file), headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @GetMapping(value = "/download",produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@RequestHeader("User-Agent")String userAgent, String fileName){
        Resource resource=new FileSystemResource("c:\\upload\\"+fileName);
        if(resource.exists()==false){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        String resourceName=resource.getFilename();
        String resourceOriginalName=resourceName.substring(resourceName.indexOf("_")+1);
        HttpHeaders header=new HttpHeaders();
        try{
            String downloadName=null;
            if(userAgent.contains("Trident")){
                log.info("IE browser");
                downloadName= URLEncoder.encode(resourceOriginalName,"UTF-8").replaceAll("\\+"," ");
            }else{
                log.info("Chrome browser");
                downloadName=new String(resourceOriginalName.getBytes("UTF-8"),"ISO-8859-1");
            }
            log.info("DownloadName name: "+downloadName);
            header.add("Content-Disposition","attachment; filename="+resourceOriginalName);
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return new ResponseEntity<Resource>(resource,header,HttpStatus.OK);
    }

    @PostMapping("/deleteFile")
    @ResponseBody
    public ResponseEntity<String> deleteFile(String fileName,String type){
        File file;
        try{
            file=new File("c:\\upload\\"+ URLDecoder.decode(fileName,"UTF-8"));
            log.info("FILE: "+file);

            file.delete();

            if(type.equals("image")){
                String largeFileName=file.getAbsolutePath().replace("s_","");
                log.info("largeFileName: "+largeFileName);

                file=new File(largeFileName);
                log.info("LARGEFILE: "+file);
                file.delete();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>("deleted",HttpStatus.OK);
    }

    private String getFolder(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Date date=new Date();
        String str=sdf.format(date);
        return str.replace("-", File.separator);
    }

    private boolean checkImageType(File file){
        try{
            String contentType= Files.probeContentType(file.toPath());

            return contentType.startsWith("image");
        }catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }
}
