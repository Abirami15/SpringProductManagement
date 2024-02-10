package com.spring.productmanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.productmanagement.dto.ProductDTO;
import com.spring.productmanagement.dto.SubProductsDTO;
import com.spring.productmanagement.repo.ProductRepo;
import com.spring.productmanagement.repo.SubProductRepo;
import com.spring.productmanagement.service.ProductService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductManagementApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductRepo productRepo;

	@Autowired
	private SubProductRepo subProductRepo;

	public ProductDTO createProductDTO(){
		List<SubProductsDTO> subProductsList = Stream.of(
				new SubProductsDTO("Lenevo", "Compact Laptop"),
				new SubProductsDTO("Dell xp", "Compact Laptop")
				// Add more SubProductsDTO objects as needed
		).collect(Collectors.toList());

		ProductDTO productDTO = new ProductDTO();
		productDTO.setName("Laptop");
		productDTO.setDesc("High-performance laptop for professionals");
		productDTO.setSubProductsDTOList(subProductsList);

		return productDTO;
	}

	@Test
	@Order(1)
	void testCreateProductWithSubProduct() throws Exception {
		ProductDTO productDTO = createProductDTO();
		ObjectMapper objectMapper = new ObjectMapper();
		String productDTOJson = objectMapper.writeValueAsString(productDTO);

		mockMvc.perform(MockMvcRequestBuilders.post("/products/create")
						.contentType(MediaType.APPLICATION_JSON)
						.content(productDTOJson))
				.andExpect(status().isCreated())
				.andExpect(content().string("Product and SubProduct created successfully"));
	}

	@Test
	@Order(2)
	public void testGetProductById_Success() throws Exception {
		long productId = 1L;
		ProductDTO productDTO=createProductDTO();

		mockMvc.perform(MockMvcRequestBuilders.get("/products/{productId}", productId))
				.andExpect(status().isOk())
				.andExpect((ResultMatcher) jsonPath("$['Product Id']").value(productId))
				.andExpect((ResultMatcher) jsonPath("$.Name").value(productDTO.getName()))
				.andExpect((ResultMatcher) jsonPath("$.Description").value(productDTO.getDesc()))
				.andExpect((ResultMatcher) jsonPath("$.SubProducts[0]['SubProduct Id']").value(1L))
				.andExpect((ResultMatcher) jsonPath("$.SubProducts[0].Name").value(productDTO.getSubProductsDTOList().get(0).getName()))
				.andExpect((ResultMatcher) jsonPath("$.SubProducts[0].Description").value(productDTO.getSubProductsDTOList().get(0).getDesc()))
				.andExpect((ResultMatcher) jsonPath("$.SubProducts[1]['SubProduct Id']").value(2L))
				.andExpect((ResultMatcher) jsonPath("$.SubProducts[1].Name").value(productDTO.getSubProductsDTOList().get(1).getName()))
				.andExpect((ResultMatcher) jsonPath("$.SubProducts[1].Description").value(productDTO.getSubProductsDTOList().get(1).getDesc()));
	}

	@Test
	@Order(3)
	public void testGetProductById_ProductNotFound() throws Exception {
		long productId = 234L;
		mockMvc.perform(MockMvcRequestBuilders.get("/products/{productId}", productId))
				.andExpect(status().isNotFound())
				.andExpect(content().string("Product not found with id " + productId));
	}

	@Test
	@Order(4)
	public void testUpdateProductAndSubProduct_Success() throws Exception{
		long productId = 1L;
		ProductDTO productDTO=createProductDTO();
		productDTO.setName("Computer");

		ObjectMapper objectMapper = new ObjectMapper();
		String productDTOJson = objectMapper.writeValueAsString(productDTO);

		mockMvc.perform(MockMvcRequestBuilders.put("/products/update/{productId}", productId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(productDTOJson))
				.andExpect(status().isOk())
				.andExpect(content().string("Product and SubProduct updated successfully"));
	}

	@Test
	@Order(5)
	public void testUpdateProductAndSubProduct_ProductNotFound() throws Exception{
		long productId=1232L;
		ProductDTO productDTO=createProductDTO();
		ObjectMapper objectMapper = new ObjectMapper();
		String productDTOJson = objectMapper.writeValueAsString(productDTO);

		mockMvc.perform(MockMvcRequestBuilders.put("/products/update/{productId}", productId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(productDTOJson))
				.andExpect(status().isNotFound())
				.andExpect(content().string("Product not found with id: " +productId));
	}

	@Test
	@Order(6)
	public void testDeleteProductWithSubProduct_Success() throws  Exception{
		long productId = 1L;
		ProductDTO productDTO=createProductDTO();
		productService.createProductWithSubProduct(productDTO);

		mockMvc.perform(MockMvcRequestBuilders.delete("/products/delete/{productId}", productId))
				.andExpect(status().isOk())
				.andExpect(content().string("Product and SubProduct deleted successfully"));
	}

	@Test
	@Order(7)
	public void testDeleteProductWithSubProduct_ProductNotFound() throws  Exception{
		long productId = 2345L;

		mockMvc.perform(MockMvcRequestBuilders.delete("/products/delete/{productId}",productId))
				.andExpect(status().isNotFound())
				.andExpect(content().string("Product not found with id: " +productId));
	}

	@Test
	@Order(8)
	void testGetAllProducts_Success() throws Exception {
		int page = 0;
		int size = 2;
		ProductDTO productDTO=createProductDTO();
		productService.createProductWithSubProduct(productDTO);

		mockMvc.perform(MockMvcRequestBuilders.get("/products/all")
						.param("page", String.valueOf(page))
						.param("size", String.valueOf(size)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content").exists())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}

	@Test
	@Order(9)
	void testGetAllProducts_NoProductsFound() throws Exception {
		int page = 0;
		int size = 2;

		productRepo.deleteAll();
		subProductRepo.deleteAll();

		mockMvc.perform(MockMvcRequestBuilders.get("/products/all")
						.param("page", String.valueOf(page))
						.param("size", String.valueOf(size)))
				.andExpect(status().isNotFound())
				.andExpect(content().string("No products available"));
	}

}
