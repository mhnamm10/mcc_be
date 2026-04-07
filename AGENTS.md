# AGENTS.md - Development Guidelines for erp_bom

## Project Overview

Spring Boot 4.0.3 + Java 17 + Maven. ERP system (BOM management) using MyBatis Plus with PostgreSQL.

## Build Commands

```bash
# Build project
mvn clean install

# Run application
mvn spring-boot:run

# Build without tests
mvn clean install -DskipTests

# Run specific test class
mvn test -Dtest=ProductionOrderServiceTest

# Run specific test method
mvn test -Dtest=ProductionOrderServiceTest#testCreate

# Package as JAR
mvn package
```

## Project Structure

```
src/main/java/com/erp/bom/
├── ErpBomApplication.java
├── config/
├── feature/
│   ├── auth/, production/, product/, material/, common/, cloudinary/
│   └── [feature]/
│       ├── controller/, service/, mapper/, entity/, dto/
└── utils/excel/
```

## Code Style Guidelines

### Naming Conventions
- **Classes**: PascalCase (`ProductionOrderService`)
- **Methods/Variables**: camelCase (`getById`, `orderNo`)
- **Constants**: UPPER_SNAKE_CASE (`MAX_FILE_SIZE`)
- **Packages**: lowercase (`com.erp.bom.feature.production`)

### Constructor Injection (Required)
```java
@Service
public class ProductionOrderService {
    private final ProductionOrderMapper productionOrderMapper;

    public ProductionOrderService(ProductionOrderMapper productionOrderMapper) {
        this.productionOrderMapper = productionOrderMapper;
    }
}
```

### Controller Guidelines
```java
@RestController
@RequestMapping("/api/production-orders")
public class ProductionOrderController {
    private static final Logger log = LoggerFactory.getLogger(ProductionOrderController.class);
    private final ProductionOrderService productionOrderService;

    public ProductionOrderController(ProductionOrderService productionOrderService) {
        this.productionOrderService = productionOrderService;
    }

    @PostMapping
    public ResponseEntity<ProductionOrder> create(@RequestBody ProductionOrder productionOrder) {
        log.info("[POST /api/production-orders] Payload: {}", productionOrder);
        return ResponseEntity.status(HttpStatus.CREATED).body(productionOrderService.create(productionOrder));
    }
}
```

### Service Layer
- Business logic in service layer
- Use MyBatis Plus `LambdaQueryWrapper` for dynamic queries
- Throw `RuntimeException` with meaningful messages for not found errors
- Return appropriate HTTP status codes (201 created, 404 not found, etc.)

### Entity/Database Layer
- Use MyBatis Plus annotations (`@TableName`, `@TableField`)
- Use enums for fixed status values
- Entity fields follow camelCase, database columns use snake_case

### Import Order
1. Java/Jakarta EE
2. Spring Framework
3. Third-party (MyBatis Plus, Lombok)
4. Internal packages

```java
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.bom.feature.production.entity.ProductionOrder;
```

### Utility Classes
```java
public class ExcelUtil {
    private ExcelUtil() {}  // Prevent instantiation
    public static final String EXCEL_CONTENT_TYPE = "application/vnd...";
}
```

### REST API Conventions
- Use plural nouns: `/api/production-orders`
- HTTP methods: GET (read), POST (create), PUT (update), DELETE (remove)
- Path variables: `/api/production-orders/{id}`
- Query parameters: `/api/production-orders?status=pending`

### DTOs
- Use `{EntityName}Request` / `{EntityName}Response` naming
- Keep DTOs in `dto/` subpackage within each feature

### Lombok Usage
- Prefer `@Getter`/`@Setter` over `@Data` when only specific methods needed
- Use `@Builder` for complex objects
- Use `@TableField` when DB column names differ

### Error Handling
- Use custom exceptions for domain-specific errors
- Include entity identifiers in error messages
- Use global `@ControllerAdvice` for exception handling

### Security
- JWT authentication implemented
- Use `@PreAuthorize` for role-based access control
- Never expose sensitive data in API responses

### Testing
- Write unit tests for service layer
- Use `@SpringBootTest` for integration tests
- Mock external dependencies

## Key Dependencies
- Spring Boot 4.0.3
- MyBatis Plus 3.5.5
- PostgreSQL, Lombok
- EasyExcel 4.0.3
- SpringDoc OpenAPI 2.3.0
- Cloudinary 2.0.0
- P6Spy 1.12.0 (SQL logging)