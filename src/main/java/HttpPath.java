

public class HttpPath {
	
	private String requestUri;
	private int questionPos;

    public HttpPath(String requestUri) {
        this.requestUri = requestUri;
        questionPos = getQuestionPos();
    }

    public String getPath() {
        if (questionPos != -1) {
        	return requestUri.substring(0, questionPos);
        }
        else {
        	return requestUri;
        }
    }

    private int getQuestionPos() {
        return requestUri.indexOf("?");
    }

    public HttpQuery getQuery() {
        if (questionPos != -1) {
        	return new HttpQuery(requestUri.substring(questionPos+1));
        }
        else {
        	return null;
        }
    }

    public String toString() {
        HttpQuery query = getQuery();
        return getPath() + (query != null ? "?" + query : "");
    }
}