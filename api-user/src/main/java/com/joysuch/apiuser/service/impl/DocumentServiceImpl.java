package com.joysuch.apiuser.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joysuch.apiuser.domain.Document;
import com.joysuch.apiuser.mapper.DocumentMapper;
import com.joysuch.apiuser.service.DocumentService;
import org.springframework.stereotype.Service;

/**
* @author admin
* @description 针对表【document(文档)】的数据库操作Service实现
* @createDate 2024-08-09 19:44:17
*/
@Service
public class DocumentServiceImpl extends ServiceImpl<DocumentMapper, Document>
    implements DocumentService {

}




