package vn.iotstar.coolenglish.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class PaginationSupportTest {

	@Test
	void shouldPaginateByThirtyRows() {
		List<Integer> values = new ArrayList<>();
		for (int i = 1; i <= 65; i++) {
			values.add(i);
		}

		PaginationSupport.Page<Integer> page1 = PaginationSupport.paginate(values, "1", 30);
		PaginationSupport.Page<Integer> page3 = PaginationSupport.paginate(values, "3", 30);

		assertEquals(30, page1.getItems().size());
		assertEquals(3, page3.getTotalPages());
		assertEquals(5, page3.getItems().size());
	}

	@Test
	void shouldClampInvalidPageToValidRange() {
		List<Integer> values = List.of(1, 2, 3);

		PaginationSupport.Page<Integer> belowRange = PaginationSupport.paginate(values, "0", 30);
		PaginationSupport.Page<Integer> aboveRange = PaginationSupport.paginate(values, "99", 30);

		assertEquals(1, belowRange.getCurrentPage());
		assertEquals(1, aboveRange.getCurrentPage());
	}
}

