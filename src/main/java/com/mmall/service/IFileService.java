package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by binlin on 2018/8/26.
 */
public interface IFileService {

    String upload(MultipartFile file, String path);
}
