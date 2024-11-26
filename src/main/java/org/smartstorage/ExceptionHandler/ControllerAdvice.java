package org.smartstorage.ExceptionHandler;


import io.grpc.StatusRuntimeException;
import org.smartstorage.Utility.ResponseModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ResponseModel> handleFileUploadException(Exception e){
        return new ResponseEntity<>(new ResponseModel()
                .setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .setMessage(e.getMessage())
                , HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StatusRuntimeException.class)
    @ResponseBody
    public  ResponseEntity<ResponseModel> handleFileWriteException(StatusRuntimeException e){
        return new ResponseEntity<>(new ResponseModel()
                .setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .setMessage(e.getMessage())
                , HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public ResponseEntity<ResponseModel> handleBadRequestException(RuntimeException e){
        return new ResponseEntity<>(new ResponseModel()
                .setStatus(HttpStatus.BAD_REQUEST.value())
                .setMessage(e.getMessage())
                , HttpStatus.BAD_REQUEST);
    }
}
