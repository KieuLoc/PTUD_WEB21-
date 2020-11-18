package application.controller.api;

import application.data.model.Product;
import application.data.model.ProductImage;
import application.data.service.ProductImageService;
import application.data.service.ProductService;
import application.model.api.BaseApiResult;
import application.model.api.DataApiResult;
import application.model.dto.ProductImageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping(path = "/api/product-image")
public class ProductImageApiController {


    @Autowired
    private ProductImageService productImageService;

    @Autowired
    private ProductService productService;

    private String[] images = {
            "https://salt.tikicdn.com/cache/280x280/ts/product/d7/4b/c4/a52dff7fc2e4dc8ac4be6631a0933199.jpg",
            "https://salt.tikicdn.com/cache/280x280/ts/product/50/b8/c5/ea2d975c22483854c70a48052e33b7f4.jpg",
            "https://salt.tikicdn.com/cache/280x280/ts/product/86/85/74/bd348eeeea93268b0cea8e3bb14cbaf9.jpg",
            "https://salt.tikicdn.com/cache/280x280/ts/product/f3/eb/69/8a7769830a82334068669457b20180b0.jpg",
            "https://salt.tikicdn.com/cache/280x280/ts/product/fb/db/ce/c94548b56dd1e7a3cd13a9e98f4b6694.jpg"
    };

    @GetMapping("/fake")
    public BaseApiResult fakeProductImage() {
        BaseApiResult result = new BaseApiResult();

        try {
            Random random = new Random();
            List<Product> productList = productService.getListAllProducts();
            for (Product product : productList) {
                if (product.getProductImageList().size() == 0) {
                    List<ProductImage> productImages = new ArrayList<>();
                    ProductImage productMainImage = new ProductImage();
                    productMainImage.setLink(product.getMainImage());
                    productMainImage.setProduct(product);
                    productMainImage.setCreatedDate(new Date());

                    productImages.add(productMainImage);
                    for (int i = 0; i < random.nextInt(2) + 1; i++) {
                        ProductImage productImage = new ProductImage();
                        productImage.setLink(images[random.nextInt(images.length)]);
                        productImage.setProduct(product);
                        productImage.setCreatedDate(new Date());

                        productImages.add(productImage);
                    }
                    productImageService.addNewListProductImages(productImages);
                }
            }
            result.setSuccess(true);
            result.setMessage("Fake list product images successfully !");
        } catch (Exception e) {
            result.setMessage(e.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PostMapping(value = "/create")
    public BaseApiResult createProductImage(@RequestBody ProductImageDTO dto) {
        DataApiResult result = new DataApiResult();

        try {
            ProductImage productImage = new ProductImage();
            productImage.setProduct(productService.findOne(dto.getProductId()));
            productImage.setCreatedDate(new Date());
            productImage.setLink(dto.getLink());
            productImageService.addNewProductImage(productImage);
            result.setData(productImage.getId());
            result.setMessage("Save product image successfully: " + productImage.getId());
            result.setSuccess(true);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @PostMapping("/update/{productImageId}")
    public BaseApiResult updateCategory(@PathVariable int productImageId,
                                        @RequestBody ProductImageDTO dto) {
        BaseApiResult result = new BaseApiResult();

        try {
            ProductImage productImage = productImageService.findOne(productImageId);
            productImage.setLink(dto.getLink());
            productImage.setCreatedDate(new Date());
            productImageService.addNewProductImage(productImage);
            result.setSuccess(true);
            result.setMessage("Update product image successfully");
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }

        return result;
    }

    @DeleteMapping("/delete/{productImageId}")
    public BaseApiResult deleteProductImage(@PathVariable int productImageId,
                                            @RequestBody ProductImageDTO dto){
        BaseApiResult result = new BaseApiResult();
        try{
            productService.deleteProduct(productImageId);
            result.setSuccess(true);
            result.setMessage("Delete Success");

        }catch (Exception e){
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }
}

