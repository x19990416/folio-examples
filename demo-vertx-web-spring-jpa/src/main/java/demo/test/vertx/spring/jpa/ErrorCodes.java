package demo.test.vertx.spring.jpa;

/**
 * Created by Administrator on 2018/3/9.
 */
public enum ErrorCodes {
    
    NO_ACTION_SPECIFIED(701,"no action header specified"),
    BAD_ACTION(702,"bad action"),
    DB_ERROR(703,"db error");
    private Integer code;
    private String message;
    private ErrorCodes(Integer code,String message) {
      this.code=code;
      this.message= message;
    }
    
    public Integer code() {
      return code;
    }
    
    public String msg() {
      return this.message;
    }
}
