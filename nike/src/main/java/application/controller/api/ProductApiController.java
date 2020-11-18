package application.controller.api;

import application.data.model.Category;
import application.data.model.Product;
import application.data.repository.ProductRepository;
import application.data.service.CategoryService;
import application.data.service.ProductService;
import application.model.api.BaseApiResult;
import application.model.api.DataApiResult;
import application.model.dto.ProductDTO;
import application.model.viewmodel.common.ProductVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping(path = "/api/product")
public class ProductApiController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductRepository productRepository;

    private String[] images = {
            "https://salt.tikicdn.com/cache/280x280/ts/product/d7/4b/c4/a52dff7fc2e4dc8ac4be6631a0933199.jpg",
            "https://salt.tikicdn.com/cache/280x280/ts/product/50/b8/c5/ea2d975c22483854c70a48052e33b7f4.jpg",
            "https://salt.tikicdn.com/cache/280x280/ts/product/86/85/74/bd348eeeea93268b0cea8e3bb14cbaf9.jpg",
            "https://salt.tikicdn.com/cache/280x280/ts/product/f3/eb/69/8a7769830a82334068669457b20180b0.jpg",
            "https://salt.tikicdn.com/cache/280x280/ts/product/fb/db/ce/c94548b56dd1e7a3cd13a9e98f4b6694.jpg"
    };
    @GetMapping("/fake")
    public BaseApiResult fakeProduct(){
        BaseApiResult result = new BaseApiResult();

        try {
            long totalProducts = productService.getTotalProducts();
            List<Category> categoryList = categoryService.getListAllCategories();
            List<Product> productList = new ArrayList<>();
            Random random = new Random();
            for(long i = totalProducts +1; i<= totalProducts + 40; i++) {
                Product product = new Product();
                product.setName("Product " + i);
                product.setShortDesc("Product " + i + " short desc");
                product.setMainImage(images[random.nextInt(images.length)]);

                /*
                 * random price
                 */
                double rangeMin = 4;
                double rangeMax = 30;
                double randomPrice = rangeMin + (rangeMax - rangeMin) * random.nextDouble();
                product.setPrice(randomPrice);

                /**
                 * random category
                 */

                product.setCategory(categoryList.get(random.nextInt(categoryList.size())));
                product.setCreatedDate(new Date());
                productList.add(product);
            }
            productService.addNewListProducts(productList);
            result.setSuccess(true);
            result.setMessage("Fake list products success !");
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @PostMapping(value = "/create")
    public BaseApiResult createProduct(@RequestBody ProductDTO dto){
        DataApiResult result = new DataApiResult();
        try {
            Product product = new Product();
            product.setName(dto.getName());
            product.setShortDesc(dto.getShortDesc());
            product.setPrice(dto.getPrice());
            product.setMainImage(dto.getMainImage());
            product.setCategory(categoryService.findOne(dto.getCategoryId()));
            product.setCreatedDate(new Date());
            productService.addNewProduct(product);
            result.setData(product.getId());
            result.setMessage("Save product successfully: " + product.getId());
            result.setSuccess(true);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @PostMapping("/update/{productId}")
    public BaseApiResult updateProduct(@PathVariable int productId,
                                       @RequestBody ProductDTO dto) {
        BaseApiResult result = new BaseApiResult();
        try {
            Product product = productService.findOne(productId);
            product.setName(dto.getName());
            product.setShortDesc(dto.getShortDesc());
            product.setPrice(dto.getPrice());
            product.setMainImage(dto.getMainImage());
            product.setCategory(categoryService.findOne(dto.getCategoryId()));
            product.setCreatedDate(new Date());
            productService.addNewProduct(product);
            result.setSuccess(true);
            result.setMessage("Update product successfully");
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }

        return result;
    }


    @GetMapping("/detail/{productId}")
    public DataApiResult detailProduct(@PathVariable int productId){
        DataApiResult result= new DataApiResult();

        try {
            Product productEntity = productService.findOne(productId);
            if(productEntity == null) {
                result.setSuccess(false);
                result.setMessage("Can't find this product");
            } else {
                ProductDTO dto = new ProductDTO();
                dto.setId(productEntity.getId());
                if(productEntity.getCategory() != null) {
                    dto.setCategoryId(productEntity.getCategory().getId());
                }
                dto.setMainImage(productEntity.getMainImage());
                dto.setName(productEntity.getName());
                dto.setPrice(productEntity.getPrice());
                dto.setShortDesc(productEntity.getShortDesc());
                dto.setCreatedDate(new Date());
                result.setSuccess(true);
                result.setData(dto);
                result.setMessage("Success");
            }
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }

        return result;
    }


    @GetMapping("/query/method-name")
    public DataApiResult testQueryNameMethod(@RequestParam(name = "name", defaultValue = "", required = false) String name){
        DataApiResult result= new DataApiResult();

        List<Product> productList = productRepository.findByNameContainingIgnoreCase(name);
        List<ProductDTO> productDTOS = new ArrayList<>();

        for (Product productEntity : productList){
            ProductDTO dto = new ProductDTO();
            dto.setId(productEntity.getId());
            if(productEntity.getCategory() != null) {
                dto.setCategoryId(productEntity.getCategory().getId());
            }
            dto.setMainImage(productEntity.getMainImage());
            dto.setName(productEntity.getName());
            dto.setPrice(productEntity.getPrice());
            dto.setShortDesc(productEntity.getShortDesc());
            dto.setCreatedDate(productEntity.getCreatedDate());

            productDTOS.add(dto);
        }

        result.setMessage("success");
        result.setData(productDTOS);
        result.setSuccess(true);
        return result;
    }

    @GetMapping("/query/jpql")
    public DataApiResult testQueryJPQL(@RequestParam(name = "name", defaultValue = "", required = false) String name){
        DataApiResult result= new DataApiResult();
        List<ProductDTO> productDTOS = productRepository.getListByNameContaining(name);

        result.setMessage("success");
        result.setData(productDTOS);
        result.setSuccess(true);
        return result;
    }

    @GetMapping("/query/native")
    public DataApiResult testQueryNative(@RequestParam(name = "name", defaultValue = "", required = false) String name){
        DataApiResult result= new DataApiResult();
        List<Object[]> data = productRepository.getListByNameContainingNative(name);

        result.setMessage("success");
        result.setData(data);
        result.setSuccess(true);
        return result;
    }
}
