package com.example.demo.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.tobato.fastdfs.domain.GroupState;
import com.github.tobato.fastdfs.domain.MateData;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.github.tobato.fastdfs.service.TrackerClient;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("fastdfs")
@Slf4j
public class FastdfsController {

	@Autowired
	private FastFileStorageClient fastFileStorageClient;

	@Autowired
	private TrackerClient trackerClient;

	@GetMapping("/content/loads")
	public void contextLoads() {
		List<GroupState> groupStates = trackerClient.listGroups();
		for (GroupState groupState : groupStates) {
			System.out.println(groupState);
		}

	}
	
	public static void main(String[] args) {
		String str = "/home/ipb/服务器列表.xlsx";
		
		System.out.println( str.substring(str.lastIndexOf("/") + 1));
	}

	@GetMapping("/content/upload")
	public void upload() {

		try {
			String filePath = "/home/ipb/ceshi.mp4";
			File file = new File(filePath);

			FileInputStream inputStream = new FileInputStream(file);
			Set<MateData> metaDataSet = new HashSet<>();
			MateData mateData = new MateData();
			mateData.setName("fileRealName");
			mateData.setValue(filePath.substring(filePath.lastIndexOf("/") + 1));
			metaDataSet.add(mateData);
			StorePath storePath = fastFileStorageClient.uploadFile(inputStream, file.length(), filePath.substring(filePath.lastIndexOf(".") + 1), metaDataSet);

			// fastFileStorageClient.uploadSlaveFile(storePath.getGroup(),storePath.getPath(),inputStream,inputStream.available(),"a_",null);
			// fastFileStorageClient.uploadSlaveFile("group1","M00/00/00/wKiAjVlpNjiAK5IHAADGA0F72jo578.jpg",inputStream,inputStream.available(),"a_",null);

			System.out.println(storePath.getGroup() + " " + storePath.getPath());

			inputStream.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@GetMapping("/content/download")
	public void download(HttpServletResponse response, @RequestParam("groupId") String groupId, @RequestParam("fileName") String fileName) {
        try {
            byte[] bytes = fastFileStorageClient.downloadFile(groupId, fileName, new DownloadByteArray());

            Set<MateData> mateDataSet = fastFileStorageClient.getMetadata(groupId, fileName);
            log.info("mateDataSet is {}", mateDataSet);
            String realFileName = "";
            
            for (MateData mateData : mateDataSet) {
            	if ( mateData.getName().equals("fileRealName") ) {
            		realFileName = mateData.getValue();
            	}
			}
            
            log.info("realFileName is {}", realFileName);
            
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(realFileName.getBytes(), "ISO-8859-1"));
            
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            toClient.write(bytes);
            toClient.flush();
            toClient.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	@GetMapping("/content/delete")
	public void deleteFile(@RequestParam("groupId") String groupId, @RequestParam("fileName") String fileName){
        fastFileStorageClient.deleteFile(groupId,fileName);
        log.info("file deleted...");
    }
	
	@GetMapping("/download")
	public void testDownload(HttpServletResponse response, @RequestParam("groupId") String groupId, @RequestParam("fileName") String fileName) {
        try {
        	
        	fileName = "鄂尔多斯党建工作分工作业跟踪.xlsx";
        	
        	File file = new File("d:/" + fileName);
        	
        	FileInputStream fis = new FileInputStream(file);
        	
        	// response.setHeader("content-type", "application/octet-stream");
            // response.setContentType("application/octet-stream");
             response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes(), "ISO-8859-1"));
        	
        	OutputStream output = response.getOutputStream();
        	
        	IOUtils.copy(fis, output);;

            output.flush();
            output.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	@Autowired
	private FdfsConfigurtion fdfsConfigurtion;
	
	@GetMapping("/valuetest")
	public void valueTest() {
		
		log.info("urls is {}", fdfsConfigurtion.getTrackerList());
		
	}
	
	@GetMapping("/testgit")
	public String testGitUpdate() {
		return "this is a test.";
	}
}

@Configuration
@ConfigurationProperties(prefix="fdfs")
@Data
class FdfsConfigurtion {
	private List<String> trackerList;
}