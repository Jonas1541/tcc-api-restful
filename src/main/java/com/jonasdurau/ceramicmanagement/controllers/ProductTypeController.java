package com.jonasdurau.ceramicmanagement.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jonasdurau.ceramicmanagement.dtos.ProductTypeDTO;
import com.jonasdurau.ceramicmanagement.services.ProductTypeService;

@RestController
@RequestMapping("/product-types")
public class ProductTypeController extends IndependentController<ProductTypeDTO, ProductTypeDTO, ProductTypeDTO, Long, ProductTypeService>{
}
