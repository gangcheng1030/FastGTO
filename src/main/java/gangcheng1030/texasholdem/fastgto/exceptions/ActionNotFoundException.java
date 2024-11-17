package gangcheng1030.texasholdem.fastgto.exceptions;

public class ActionNotFoundException extends RuntimeException{
    public ActionNotFoundException(String errmsg){
        super(errmsg);
    }
}