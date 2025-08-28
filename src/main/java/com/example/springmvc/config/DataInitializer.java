package com.example.springmvc.config;

import com.example.springmvc.entity.*;
import com.example.springmvc.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        // Check if data already exists
        if (userRepository.count() > 0) {
            return; // Data already initialized
        }        // Create admin user
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("{noop}admin123"); // Store as plain string with noop prefix
        admin.setEmail("admin@cuahangmaytinh.com");
        admin.setFullName("Quản trị viên hệ thống");
        admin.setPhone("+84 (028) 123-4567");
        admin.setAddress("123 Đường Quản trị, TP.HCM, Việt Nam");
        admin.setRole(User.Role.ADMIN);
        admin.setEnabled(true);
        userRepository.save(admin);

        // Create customer user
        User customer = new User();
        customer.setUsername("customer");
        customer.setPassword("{noop}customer123"); // Store as plain string with noop prefix
        customer.setEmail("khachhang@example.com");
        customer.setFullName("Nguyễn Văn An");
        customer.setPhone("+84 (028) 987-6543");
        customer.setAddress("456 Đường Khách hàng, TP.HCM, Việt Nam");
        customer.setRole(User.Role.CUSTOMER);
        customer.setEnabled(true);
        userRepository.save(customer);// Create categories
        List<Category> categories = Arrays.asList(
            createCategory("Laptop", "Máy tính xách tay cho công việc và giải trí"),
            createCategory("Máy tính để bàn", "Hệ thống máy tính để bàn hiệu suất cao"),
            createCategory("PC Gaming", "Máy tính chơi game và linh kiện tùy chỉnh"),
            createCategory("Màn hình", "Màn hình LCD, LED và OLED"),
            createCategory("Phụ kiện", "Bàn phím, chuột và các thiết bị ngoại vi khác"),
            createCategory("Linh kiện", "CPU, GPU, RAM và các bộ phận máy tính khác")
        );
        categoryRepository.saveAll(categories);

        // Create products
        Category laptopsCategory = categories.get(0);
        Category desktopsCategory = categories.get(1);
        Category gamingCategory = categories.get(2);
        Category monitorsCategory = categories.get(3);
        Category accessoriesCategory = categories.get(4);
        Category componentsCategory = categories.get(5);        List<Product> products = Arrays.asList(
            // Laptops
            createProduct("MacBook Pro 14-inch", "Chip Apple M2 Pro, 16GB RAM, 512GB SSD", 
                new BigDecimal("52000000"), 15, "Apple", "MacBook Pro 14", laptopsCategory,
                "Bộ xử lý: Apple M2 Pro\nRAM: 16GB bộ nhớ thống nhất\nLưu trữ: 512GB SSD\nMàn hình: 14.2-inch Liquid Retina XDR\nPin: Lên đến 18 giờ",
                "https://images.unsplash.com/photo-1541807084-5c52b6b3adef?w=400"),
            
            createProduct("Dell XPS 13", "Intel Core i7, 16GB RAM, 1TB SSD", 
                new BigDecimal("33500000"), 20, "Dell", "XPS 13", laptopsCategory,
                "Bộ xử lý: Intel Core i7-1260P\nRAM: 16GB LPDDR5\nLưu trữ: 1TB PCIe NVMe SSD\nMàn hình: 13.4-inch FHD+\nPin: Lên đến 12 giờ",
                "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400"),
            
            createProduct("HP Spectre x360", "Intel Core i5, 8GB RAM, 256GB SSD", 
                new BigDecimal("23500000"), 25, "HP", "Spectre x360", laptopsCategory,
                "Bộ xử lý: Intel Core i5-1235U\nRAM: 8GB DDR4\nLưu trữ: 256GB SSD\nMàn hình: 13.5-inch 2K Touch\nPin: Lên đến 10 giờ",
                "https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?w=400"),

            // Desktop Computers
            createProduct("iMac 24-inch", "Chip Apple M2, 8GB RAM, 256GB SSD", 
                new BigDecimal("33500000"), 12, "Apple", "iMac 24", desktopsCategory,
                "Bộ xử lý: Chip Apple M2\nRAM: 8GB bộ nhớ thống nhất\nLưu trữ: 256GB SSD\nMàn hình: 24-inch 4.5K Retina\nKết nối: Wi-Fi 6, Bluetooth 5.3",
                "https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=400"),

            createProduct("HP Pavilion Desktop", "AMD Ryzen 5, 16GB RAM, 512GB SSD", 
                new BigDecimal("18000000"), 18, "HP", "Pavilion", desktopsCategory,
                "Bộ xử lý: AMD Ryzen 5 5600G\nRAM: 16GB DDR4\nLưu trữ: 512GB NVMe SSD\nĐồ họa: AMD Radeon Graphics\nHĐH: Windows 11 Home",
                "https://images.unsplash.com/photo-1587831990711-23ca6441447b?w=400"),

            // Gaming PCs
            createProduct("ASUS ROG Strix GT35", "Intel Core i7, 32GB RAM, RTX 4070", 
                new BigDecimal("64500000"), 8, "ASUS", "ROG Strix GT35", gamingCategory,
                "Bộ xử lý: Intel Core i7-12700KF\nRAM: 32GB DDR4\nGPU: NVIDIA GeForce RTX 4070\nLưu trữ: 1TB NVMe SSD\nLàm mát: Tản nhiệt nước",
                "https://images.unsplash.com/photo-1593640408182-31c70c8268f5?w=400"),            createProduct("Alienware Aurora R13", "Intel Core i9, 32GB RAM, RTX 4080", 
                new BigDecimal("85000000"), 5, "Dell", "Aurora R13", gamingCategory,
                "Bộ xử lý: Intel Core i9-12900F\nRAM: 32GB DDR5\nGPU: NVIDIA GeForce RTX 4080\nLưu trữ: 1TB NVMe SSD\nLàm mát: Tản nhiệt nước với RGB",
                "https://images.unsplash.com/photo-1551698618-1dfe5d97d256?w=400"),            // Monitors
            createProduct("Samsung 27\" 4K Monitor", "27-inch 4K UHD, HDR10, USB-C", 
                new BigDecimal("10300000"), 30, "Samsung", "U28E590D", monitorsCategory,
                "Kích thước: 27 inches\nĐộ phân giải: 3840x2160 (4K UHD)\nTấm nền: VA\nTần số quét: 60Hz\nKết nối: HDMI, DisplayPort, USB-C",
                "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=400"),

            createProduct("LG UltraWide 34\" Monitor", "34-inch 21:9, 144Hz, G-Sync", 
                new BigDecimal("15500000"), 22, "LG", "34GN850", monitorsCategory,
                "Kích thước: 34 inches\nĐộ phân giải: 3440x1440\nTấm nền: IPS\nTần số quét: 144Hz\nTính năng: G-Sync Compatible, HDR10",
                "https://images.unsplash.com/photo-1585792180666-f7347c490ee2?w=400"),            // Accessories
            createProduct("Logitech MX Master 3", "Chuột không dây cao cấp với cuộn chính xác", 
                new BigDecimal("2600000"), 50, "Logitech", "MX Master 3", accessoriesCategory,
                "Loại: Chuột không dây\nKết nối: Bluetooth, USB receiver\nPin: Lên đến 70 ngày\nTính năng: Bánh xe cuộn chính xác, điều khiển cử chỉ",
                "https://images.unsplash.com/photo-1527814050087-3793815479db?w=400"),

            createProduct("Mechanical Gaming Keyboard", "Bàn phím gaming cơ với đèn nền RGB", 
                new BigDecimal("3900000"), 35, "Corsair", "K95 RGB", accessoriesCategory,
                "Loại: Bàn phím gaming cơ\nSwitch: Cherry MX Red\nĐèn nền: RGB từng phím\nTính năng: Phím macro, USB passthrough",
                "https://images.unsplash.com/photo-1541140532154-b024d705b90a?w=400"),            // Components
            createProduct("NVIDIA RTX 4070 Graphics Card", "12GB GDDR6X, Ray Tracing, DLSS 3", 
                new BigDecimal("15500000"), 15, "NVIDIA", "RTX 4070", componentsCategory,
                "Bộ nhớ: 12GB GDDR6X\nTốc độ cơ bản: 1920 MHz\nTốc độ tăng: 2475 MHz\nTính năng: Ray Tracing, DLSS 3.0, AV1 Encode",
                "https://images.unsplash.com/photo-1591488320449-011701bb6704?w=400"),

            createProduct("AMD Ryzen 7 7700X", "Bộ xử lý 8 nhân, 16 luồng, tăng tốc 4.5GHz", 
                new BigDecimal("10300000"), 25, "AMD", "7700X", componentsCategory,
                "Nhân: 8\nLuồng: 16\nTốc độ cơ bản: 4.5 GHz\nTốc độ tăng: 5.4 GHz\nSocket: AM5\nTDP: 105W",
                "https://images.unsplash.com/photo-1555617981-dac3880eac6e?w=400")
        );

        productRepository.saveAll(products);

        System.out.println("Sample data initialized successfully!");
        System.out.println("Admin login: admin / admin123");
        System.out.println("Customer login: customer / customer123");
    }

    private Category createCategory(String name, String description) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        return category;
    }

    private Product createProduct(String name, String description, BigDecimal price, 
                                 Integer stock, String brand, String model, Category category,
                                 String specifications, String imageUrl) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStockQuantity(stock);
        product.setBrand(brand);
        product.setModel(model);
        product.setCategory(category);
        product.setSpecifications(specifications);
        product.setImageUrl(imageUrl);
        product.setActive(true);
        return product;
    }
}
