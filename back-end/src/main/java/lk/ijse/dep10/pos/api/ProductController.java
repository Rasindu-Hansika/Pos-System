package lk.ijse.dep10.pos.api;


import lk.ijse.dep10.pos.dto.CustomerDTO;
import lk.ijse.dep10.pos.dto.ProductDTO;
import lk.ijse.dep10.pos.dto.ResponseErrorDTO;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/products")
@CrossOrigin
public class ProductController {
    @Autowired
    public BasicDataSource bds;
    @PostMapping
    public ResponseEntity<?> saveProduct (@RequestBody ProductDTO product){
        try (var connection = bds.getConnection()) {
            var pstm = connection.prepareStatement("insert into products(description, quantity, price) values (?,?,?)", Statement.RETURN_GENERATED_KEYS);
            pstm.setString(1, product.getDescription());
            pstm.setInt(2, product.getQuantity());
            pstm.setBigDecimal(3, product.getPrice());
            pstm.executeUpdate();
            var generatedKeys = pstm.getGeneratedKeys();
            generatedKeys.next();
            int code= generatedKeys.getInt(1);
            product.setCode(code);
            return new ResponseEntity<>(product, HttpStatus.CREATED);
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) {
                return new ResponseEntity<>(new ResponseErrorDTO(HttpStatus.CONFLICT.value(), e.getMessage()), HttpStatus.CONFLICT);
            } else {
                return new ResponseEntity<>(new ResponseErrorDTO(500, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @GetMapping
    public ResponseEntity<?> getProducts(@RequestParam(value = "q",required = false) String query){
        if(query==null) query="";
        try (var connection = bds.getConnection()) {
            var pstm = connection.prepareStatement("select  * from  products where  code like  ? or description like  ? or quantity like ? or price like ?");
            query = "%" + query + "%";
            for (int i = 1; i <=4; i++) {
                pstm.setString(i,query);
            }
            var rst = pstm.executeQuery();
            List<ProductDTO> productDTOs = new ArrayList<>();
            while (rst.next()){
                var code = rst.getInt("code");
                var description = rst.getString("description");
                var quantity = rst.getInt("quantity");
                var price = rst.getBigDecimal("price");
                productDTOs.add(new ProductDTO(code, description, quantity, price));
            }
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("X-count",productDTOs.size()+"");
            return new ResponseEntity<>(productDTOs,httpHeaders, HttpStatus.OK);

        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ResponseErrorDTO(500,e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
