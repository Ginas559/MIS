package vn.iotstar.coolenglish.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

class CharacterEncodingFilterTest {

	private final CharacterEncodingFilter filter = new CharacterEncodingFilter();

	@Test
	void shouldSetUtf8ForRequestAndResponse() throws ServletException, IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/admin/roadmap-management");
		MockHttpServletResponse response = new MockHttpServletResponse();
		AtomicBoolean chainCalled = new AtomicBoolean(false);

		FilterChain chain = (req, res) -> chainCalled.set(true);
		filter.doFilter(request, response, chain);

		assertEquals("UTF-8", request.getCharacterEncoding());
		assertEquals("UTF-8", response.getCharacterEncoding());
		assertTrue(chainCalled.get());
	}
}

