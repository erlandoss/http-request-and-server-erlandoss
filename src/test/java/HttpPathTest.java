import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class HttpPathTest {

    @Test
    public void shouldSeparatePathAndQuery() {
        HttpPath path = new HttpPath("/urlecho?status=200");
        assertThat(path.getPath()).isEqualTo("/urlecho");
        assertThat(path.getQuery().toString()).isEqualTo("status=200");
        assertThat(path.toString()).isEqualTo("/urlecho?status=200");
        assertThat(path.getQuery().getParameter("status")).isEqualTo("200");
    }

    @Test
    public void shouldHandleUriWithoutQuery() {
        HttpPath path = new HttpPath("/myapp/echo");
        assertThat(path.getPath()).isEqualTo("/myapp/echo");
        assertThat(path.getQuery()).isNull();
        assertThat(path.toString()).isEqualTo("/myapp/echo");
    }

    @Test
    public void shouldReadParameters() {
        HttpPath path = new HttpPath("?status=403&body=mer+bl%E5b%E6r+%26+jordb%E6r");
        assertThat(path.getQuery().getParameter("status")).isEqualTo("403");
        assertThat(path.getQuery().getParameter("body")).isEqualTo("mer blåbær & jordbær");
    }

    @Test
    public void shouldCreateQuery() {
        HttpQuery query = new HttpQuery("status=200");
        query.addParameter("body", "mer blåbær");
        assertThat(query.toString()).isEqualTo("status=200&body=mer+bl%E5b%E6r");
    }

}