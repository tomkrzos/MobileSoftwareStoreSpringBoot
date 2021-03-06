package com.java.softwarestore.utils;

import com.java.softwarestore.exceptions.FtpException;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Component
public class FtpTransferHandler {

    private static final Logger LOG = Logger.getLogger(FtpTransferHandler.class);
    private static final int FTP_CODE_FILE_UNAVAILABLE = 550;
    private static final String BACKSLASH = "/";
    @Autowired
    private ProgramFileUtils programFileUtils;
    @Value("${ftp.host}")
    private String ftpHost;
    @Value("${ftp.user}")
    private String ftpUser;
    @Value("${ftp.password}")
    private String ftpPass;
    @Value("${ftp.main.upload.dir.path}")
    private String mainUploadDirPath;

    public void uploadFiles(Map<String, File> files, String targetUploadDir) throws IOException, FtpException {
        FTPClient ftp = new FTPClient();
        ftp.connect(ftpHost);

        try {
            ftp.enterLocalPassiveMode();
            if (!ftp.login(ftpUser, ftpPass)) {
                throw new FtpException("Failed to log in: " + ftp.getReplyString());
            }

            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            String targetUploadPath = mainUploadDirPath + targetUploadDir;

            if (!directoryExists(ftp, targetUploadPath)) {
                if (!ftp.makeDirectory(targetUploadPath)) {
                    throw new FtpException("Failed to create directory: " + targetUploadPath + ", error: " + ftp
                            .getReplyString());
                }
                LOG.debug("Directory created: " + targetUploadPath);
            }

            for (Map.Entry<String, File> file : files.entrySet()) {
                LOG.debug("Transferring file by ftp: " + file);
                try (InputStream in = new FileInputStream(file.getValue())) {
                    if (!ftp.storeFile(targetUploadPath + BACKSLASH + file.getValue().getName(), in)) {
                        throw new FtpException("Ftp file transfer failed: " + ftp.getReplyString());
                    }
                }
            }
        } finally {
            LOG.debug("Attempting to remove extracted files' parent directory");
            programFileUtils.removeFileOrDir(files.values().iterator().next().getAbsoluteFile().getParentFile());
            ftp.disconnect();
        }
    }

    private boolean directoryExists(FTPClient ftp, String dirPath) throws IOException {
        ftp.changeWorkingDirectory(dirPath);
        return ftp.getReplyCode() != FTP_CODE_FILE_UNAVAILABLE;
    }
}
