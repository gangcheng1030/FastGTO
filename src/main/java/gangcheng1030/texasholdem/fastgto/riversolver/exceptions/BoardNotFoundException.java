package gangcheng1030.texasholdem.fastgto.riversolver.exceptions;

/**
 * Created by huangxuefeng on 2019/10/9.
 * contains code for board not found
 */
public class BoardNotFoundException extends Exception{
    public BoardNotFoundException(String errmsg){
        super(errmsg);
    }
}
