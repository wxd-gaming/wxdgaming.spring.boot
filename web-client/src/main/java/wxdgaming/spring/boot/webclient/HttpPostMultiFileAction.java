package wxdgaming.spring.boot.webclient;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

import java.io.File;

/**
 * http post file
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 20:21
 */
@Getter
@Setter
@Accessors(chain = true)
public class HttpPostMultiFileAction extends HttpPostMultiAction<HttpPostMultiFileAction> {

    public HttpPostMultiFileAction(CloseableHttpClient closeableHttpClient, String url) {
        super(closeableHttpClient, url);
    }

    public HttpPostMultiFileAction addFile(String filePath) {
        File file1 = new File(filePath);
        this.getParamMap().put(file1.getName(), file1);
        return this;
    }

    public HttpPostMultiFileAction addFile(File file) {
        this.getParamMap().put(file.getName(), file);
        return this;
    }

}
