package com.singlesoft.web.controller;

import java.nio.file.Files;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.singlesoft.web.model.Customer;
import com.singlesoft.web.model.ServiceModel;
import com.singlesoft.web.model.User;
import com.singlesoft.web.repository.CustomerRepository;
import com.singlesoft.web.repository.ServiceRepository;
import com.singlesoft.web.repository.UserRepository;

import io.jsonwebtoken.io.IOException;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;

@RestController()
@RequestMapping("/api/services")

public class ServiceController {

    @Autowired
    private ServiceRepository serviceRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    //Development path
    private String imgDir = "/home/edie/script/java/projects/Web/img/";
    //Production path
    //private String imgDir = "/home/edie/api/java/img/";

    // To Return a Service Web Page 
    @RequestMapping("/web/{id}")
    public ModelAndView home(@PathVariable("id") String id) {
		Optional<ServiceModel> optionalService = serviceRepository.findById(id);
		if(optionalService.isPresent()){
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.setViewName("index");
			modelAndView.addObject("id", id);
			return modelAndView;
		}else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Service Not Found.");
		}
    }

    // To Get all services
    @GetMapping("/all")
    public List<ServiceModel> getAllServices(HttpServletRequest request) {
    	// Check User permission by Id at JWT Token
        String token = request.getHeader("Authorization").substring(7);
        DecodedJWT jwt = JWT.decode(token);
        Long userId = jwt.getClaim("userId").asLong();
        User currentUser = userRepository.findById(userId).orElse(null);
        if (!currentUser.getUserType().equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized to create service");
        }
    	// To set Customer NumServices at sending objects
        List<Customer> customers = customerRepository.findAll();
        for (Customer customer : customers) {
            int numServices = serviceRepository.countByCustomer(customer);
            customer.setNumServices(numServices);
        }
    	// To set User NumServices at sending objects
        List<User> users = userRepository.findAll();
        for (User user : users) {
            int numServices = serviceRepository.countByUser(user);
            user.setNumServices(numServices);
        }
        // Return Objects
        return serviceRepository.findAll();
    }

    // To Get All User's Services
    @GetMapping
    public List<ServiceModel> getAllUserServices(HttpServletRequest request) {
    	// To Get User Id at Token
        String token = request.getHeader("Authorization").substring(7);
        DecodedJWT jwt = JWT.decode(token);
        Long userId = jwt.getClaim("userId").asLong();
        User currentUser = userRepository.findById(userId).orElse(null);
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }

    	// To set Customer NumServices at sending objects
        List<Customer> customers = customerRepository.findAll();
        for (Customer customer : customers) {
            int numServices = serviceRepository.countByCustomer(customer);
            customer.setNumServices(numServices);
        }


        // To Return Services
        return serviceRepository.findByUser(currentUser);
    }

    // To Get All User's Services by id
    @GetMapping("/user/{id}")
    public List<ServiceModel> getAllUserServices(HttpServletRequest request,@PathVariable("id") long id) {
    	// Check User permission by Id at JWT Token
        String token = request.getHeader("Authorization").substring(7);
        DecodedJWT jwt = JWT.decode(token);
        Long userId = jwt.getClaim("userId").asLong();
        User currentUser = userRepository.findById(userId).orElse(null);

        if (!currentUser.getUserType().equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized to create service");
        }

        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }

    	// To set NumServices at sending objects
        List<Customer> customers = customerRepository.findAll();
        for (Customer customer : customers) {
            int numServices = serviceRepository.countByCustomer(customer);
            customer.setNumServices(numServices);
        }

    	// To set User NumServices at sending objects
        List<User> users = userRepository.findAll();
        for (User userfind : users) {
            int numServices = serviceRepository.countByUser(userfind);
            user.setNumServices(numServices);
        }

        // To Return Services
        return serviceRepository.findByUser(user);
    }

    // To Get a Service by ID
    @GetMapping("/id/{id}")
    public ResponseEntity<ServiceModel> getServiceById(@PathVariable("id") String id) {
        Optional<ServiceModel> optionalService = serviceRepository.findById(id);
        if (optionalService.isPresent()) {
        	ServiceModel service = optionalService.get();
        	service.getUser().setPassword("");
            return ResponseEntity.ok(service);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // To Get all services by Customer 
    @GetMapping("/customer/{customerId}")
    public List<ServiceModel> getServicesByCustomerId(HttpServletRequest request,@PathVariable("customerId") Long customerId) {
		// To Get User Id at Token
        String token = request.getHeader("Authorization").substring(7);
        DecodedJWT jwt = JWT.decode(token);
        Long userId = jwt.getClaim("userId").asLong();

        // Set user by ID
        User currentUser = userRepository.findById(userId).orElse(null);
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }       
        
        // Set customer numServices
        Customer currentCustomer = customerRepository.findById(customerId).orElse(null);
        List<Customer> customers = customerRepository.findAll();
        for (Customer customer : customers) {
            int numServices = serviceRepository.countByCustomer(customer);
            customer.setNumServices(numServices);
        }

    	// To set User NumServices at sending objects
        List<User> users = userRepository.findAll();
        for (User user : users) {
            int numServices = serviceRepository.countByUser(user);
            user.setNumServices(numServices);
        }

        if (currentCustomer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }
        // Return by user and customer
        if (currentUser.getUserType().equals("ADMIN")) {
			return serviceRepository.findByCustomer(currentCustomer);
        }
        return serviceRepository.findByUserAndCustomer(currentUser, currentCustomer);
    }

    // To Create New Service, with authorization checking
    @PostMapping
    public ResponseEntity<ServiceModel> createService(HttpServletRequest request, @RequestBody ServiceModel service) {
    	// Check if User Id at JWT Token is equal to the Request or user is a admin
        String token = request.getHeader("Authorization").substring(7);
        DecodedJWT jwt = JWT.decode(token);
        Long userId = jwt.getClaim("userId").asLong();
        User currentUser = userRepository.getById(userId);
        User userInService = userRepository.getById(service.getUser().getId());

        if (userId.equals(userInService.getId()) || currentUser.getUserType().equals("ADMIN") && userInService.getUserType().equals("USER") ) {
			// Create New Service
			service.setId();
			service.setFinalPrice();
			service.setTimestamp();
			ServiceModel savedService = serviceRepository.save(service);
			return new ResponseEntity<>(savedService, HttpStatus.CREATED);
        }else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized to create service");
        }
    }

    // To Update a Created service
    @PutMapping("/{id}")
    public ResponseEntity<ServiceModel> updateService(HttpServletRequest request,@PathVariable("id") String id, @RequestBody ServiceModel service) {
    	// Check if User Id at JWT Token is equal to the Request
        String token = request.getHeader("Authorization").substring(7);
        DecodedJWT jwt = JWT.decode(token);
        Long userId = jwt.getClaim("userId").asLong();
        User currentUser = userRepository.findById(userId).orElse(null);
        User userInService = userRepository.getById(service.getUser().getId());

        if (userId.equals(userInService.getId()) || currentUser.getUserType().equals("ADMIN") && userInService.getUserType().equals("USER") ) {
			// Set New attributes
			Optional<ServiceModel> optionalService = serviceRepository.findById(id);
			if (optionalService.isPresent()) {
				ServiceModel updatedService = optionalService.get();
				updatedService.setModel(service.getModel());
				updatedService.setTag(service.getTag());
				updatedService.setDescription(service.getDescription());
				updatedService.setPartCost(service.getPartCost());
				updatedService.setLaborTax(service.getLaborTax());
				updatedService.setPayed(service.getPayed());
				updatedService.setDiscount(service.getDiscount());
				//updatedService.setCustomer(service.getCustomer());
				//updatedService.setUser(service.getUser());
				updatedService.setStatus(service.getStatus());
				updatedService.setPayway(service.getPayway());
				updatedService.setFinalPrice();
				updatedService.setPrevisionTime(service.getPrevisionTime());
				ServiceModel savedService = serviceRepository.save(updatedService);
				return ResponseEntity.ok(savedService);
			} else {
				return ResponseEntity.notFound().build();
			}
        }else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized to create service");
        }
	}

    // To Delete a service by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(HttpServletRequest request,@PathVariable("id") String id) {
    	// Check User permission by Id at JWT Token
        String token = request.getHeader("Authorization").substring(7);
        DecodedJWT jwt = JWT.decode(token);
        Long userId = jwt.getClaim("userId").asLong();
        User currentUser = userRepository.findById(userId).orElse(null);

        if (currentUser.getUserType().equals("ADMIN")) {

			// Delete Function
			Optional<ServiceModel> optionalService = serviceRepository.findById(id);
			if (optionalService.isPresent() && optionalService.get().getUser().getUserType().equals("USER")|| optionalService.isPresent() && optionalService.get().getUser().getId().equals(currentUser.getId())) {
				serviceRepository.delete(optionalService.get());
				return ResponseEntity.noContent().build();
			} else {
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized to create service");
			}
        }else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized to create service");
        }
    }

    // Get The name of all service images
    // Security Already Checked
    @GetMapping("/img/filenames/{id}")
    public ResponseEntity<List<String>> getImageNames(@PathVariable("id") String id) throws java.io.IOException {
    	// Checks if service exists
        Optional<ServiceModel> service = serviceRepository.findById(id);
        if (!service.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Service Not Found.");
		}

		// Find images names function
		Path serviceDirPath = Paths.get(imgDir + id);
		if (!Files.exists(serviceDirPath)) {
			//return ResponseEntity.notFound().build();
			List<String> empty = new ArrayList<>();
			return ResponseEntity.ok(empty);
		}
		try (Stream<Path> stream = Files.list(serviceDirPath)) {
			List<String> names = stream
				.filter(file -> !Files.isDirectory(file))
				.map(Path::getFileName)
				.map(Path::toString)
				.collect(Collectors.toList());
			return ResponseEntity.ok(names);
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
    }

    // Download a image from a service
    // Security Already Checked
    @GetMapping("/img/{id}")
    public ResponseEntity<Resource> imageDownload(@PathVariable("id") String id, @RequestParam("filename") String filename, HttpServletRequest request) throws IOException, java.io.IOException {
    	// Checks if service exists
        Optional<ServiceModel> service = serviceRepository.findById(id);
        if (!service.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Service Not Found.");
		}

        // Sanitize the id and filename parameters to prevent directory traversal attacks
        String cleanId = id.replaceAll("[^\\w\\d-]", "");
        String cleanFilename = filename.replaceAll("[^\\w\\d-\\.]", "");

        // Check if the filename is valid
        if (!cleanFilename.endsWith(".jpg")) {
            throw new IllegalArgumentException("Invalid filename: " + filename);
        }

        // Construct the file path and retrieve the file
        Path imgPath = Paths.get(imgDir, cleanId, cleanFilename);
        File file = imgPath.toFile();
        if (!file.exists()) {
            throw new FileNotFoundException("Image not found for id " + id);
        }

        // Wrap the file in a ByteArrayResource
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(file.toPath()));

        // Set the response headers and return the resource
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName());
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(file.length());
        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

	// Upload new Image
    // Security Already Checked
    @PostMapping("/img/{id}")
    public ResponseEntity<String> imageUpload(HttpServletRequest request, @PathVariable("id") String id, @RequestParam("file") MultipartFile file) throws java.io.IOException {
        // Check if User Id at JWT Token is equal to the Request
        String token = request.getHeader("Authorization").substring(7);
        DecodedJWT jwt = JWT.decode(token);
        Long userId = jwt.getClaim("userId").asLong();
        User currentUser = userRepository.findById(userId).orElse(null);
        Optional<ServiceModel> service = serviceRepository.findById(id);
        User userInService = service.get().getUser();

    	// Checks if service exists
        if (!service.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Service Not Found.");
		}

        if (userId.equals(userInService.getId()) || currentUser.getUserType().equals("ADMIN") && userInService.getUserType().equals("USER")) {
            // Upload Function
            Optional<ServiceModel> optionalService = serviceRepository.findById(id);
            if (optionalService.isPresent()) {
                try {
                    String serviceDirName = id;
                    String fileName = file.getOriginalFilename();

                    // Only allow .jpg files
                    if (!fileName.endsWith(".jpg")) {
                        throw new IllegalArgumentException("Invalid file type. Only JPG files are allowed.");
                    }

                    // Prevent path traversal attacks by checking file name for relative path characters
                    if (fileName.contains("..") || fileName.contains("/")) {
                        throw new IllegalArgumentException("Invalid file name: " + fileName);
                    }

                    ServiceModel updatedService = optionalService.get();
                    updatedService.addImgName(fileName);
                    serviceRepository.save(updatedService);

                    Path serviceDirPath = Paths.get(imgDir + serviceDirName);
                    if (!Files.exists(serviceDirPath)) {
                        Files.createDirectory(serviceDirPath);
                    }

                    Path targetPath = serviceDirPath.resolve(fileName);
                    Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                    return ResponseEntity.ok("File uploaded successfully.");
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file.");
                }
            } else {
                return ResponseEntity.notFound().build();
            }

        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized to create service");
        }
    }

	// Delete image from a service
    // Security Already Checked
    @DeleteMapping("/img/{id}")
    public ResponseEntity<String> deleteImage(HttpServletRequest request,@PathVariable("id") String id, @RequestParam("filename") String filename) {
        // Check if User Id at JWT Token is equal to the Request
        String token = request.getHeader("Authorization").substring(7);
        DecodedJWT jwt = JWT.decode(token);
        Long userId = jwt.getClaim("userId").asLong();
        User currentUser = userRepository.findById(userId).orElse(null);
        Optional<ServiceModel> service = serviceRepository.findById(id);
        User userInService = service.get().getUser();

        if (!service.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Service Not Found.");
		}

        if (userId.equals(userInService.getId()) || currentUser.getUserType().equals("ADMIN") && userInService.getUserType().equals("USER")) {
            // Clean filename to avoid path traversal
            String cleanFilename = FilenameUtils.getName(filename);
            // Check file extension
            String fileExtension = FilenameUtils.getExtension(cleanFilename);
            if (!"jpg".equalsIgnoreCase(fileExtension)) {
                throw new IllegalArgumentException("Invalid file extension. Only .jpg files are allowed.");
            }
            // Delete Function
            Path imagePath = Paths.get(imgDir, id, cleanFilename).normalize();
            if (!imagePath.startsWith(Paths.get(imgDir, id))) {
                throw new IllegalArgumentException("Invalid filename: " + filename);
            }
            File file = imagePath.toFile();
            if (file.exists()) {
                file.delete();
                return ResponseEntity.ok("Image " + cleanFilename + " deleted");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image " + cleanFilename + " not found");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized to delete image");
        }
    }



    // TODO insert security user verification by token
    
    // Download All service images as a compressed file
    @GetMapping("/img/zip/{id}")
    public ResponseEntity<Resource> downloadImages(@PathVariable("id") String id) throws IOException, java.io.IOException {

        Optional<ServiceModel> optionalService = serviceRepository.findById(id);
        if (optionalService.isPresent()) {

            Path serviceDirPath = Paths.get(imgDir + id);
            if (Files.exists(serviceDirPath) && Files.isDirectory(serviceDirPath)) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ZipOutputStream zos = new ZipOutputStream(baos);

                Files.list(serviceDirPath)
                        .filter(Files::isRegularFile)
                        .forEach(file -> {
                            try {
                                ZipEntry zipEntry = new ZipEntry(file.getFileName().toString());
                                zos.putNextEntry(zipEntry);
                                zos.write(Files.readAllBytes(file));
                                zos.closeEntry();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        });

                zos.close();
                ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + id + ".zip")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .contentLength(baos.size())
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }



    /*
    // Upload new service Image
    @PostMapping("/img/{id}")
    public ResponseEntity<String> imageUpload(HttpServletRequest request,@PathVariable("id") String id, @RequestParam("file") MultipartFile file) throws java.io.IOException {
		// Check if User Id at JWT Token is equal to the Request
        String token = request.getHeader("Authorization").substring(7);
        DecodedJWT jwt = JWT.decode(token);
        Long userId = jwt.getClaim("userId").asLong();
        User currentUser = userRepository.findById(userId).orElse(null);
        Optional<ServiceModel> service = serviceRepository.findById(id);
		User userInService = service.get().getUser();

        if (userId.equals(userInService.getId()) || currentUser.getUserType().equals("ADMIN") && userInService.getUserType().equals("USER") ) {

        	// Upload Function
			Optional<ServiceModel> optionalService = serviceRepository.findById(id);
			if (optionalService.isPresent()) {
				try {
					String serviceDirName = id;
					String fileName = file.getOriginalFilename();
					
					ServiceModel updatedService = optionalService.get();
					updatedService.addImgName(fileName);
					serviceRepository.save(updatedService); 

					Path serviceDirPath = Paths.get(imgDir + serviceDirName);
					if (!Files.exists(serviceDirPath)) {
						Files.createDirectory(serviceDirPath);
					}

					Path targetPath = serviceDirPath.resolve(fileName);
					Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
					return ResponseEntity.ok("File uploaded successfully.");
				} catch (IOException e) {
					e.printStackTrace();
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file.");
				}
			} else {
				return ResponseEntity.notFound().build();
			}

		}else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized to create service");
        }
    }*/
    /*
    // Delete image from a service
    @DeleteMapping("/img/{id}")
    public ResponseEntity<String> deleteImage(HttpServletRequest request,@PathVariable("id") String id, @RequestParam("filename") String filename) {
		// Check if User Id at JWT Token is equal to the Request
        String token = request.getHeader("Authorization").substring(7);
        DecodedJWT jwt = JWT.decode(token);
        Long userId = jwt.getClaim("userId").asLong();
        User currentUser = userRepository.findById(userId).orElse(null);
        Optional<ServiceModel> service = serviceRepository.findById(id);
		User userInService = service.get().getUser();


        if (userId.equals(userInService.getId()) || currentUser.getUserType().equals("ADMIN") && userInService.getUserType().equals("USER") ) {
			//File file = new File(imgDir + id + "/" + filename);

        	// To clean file name due security issues
        	String cleanFilename = FilenameUtils.getName(filename);
        	if (cleanFilename.contains("..") || cleanFilename.contains("/") || !cleanFilename.contains(".jpg")) {
        	    throw new IllegalArgumentException("Invalid filename: " + filename);
        	}
        	// Delete Function
        	File file = new File(imgDir + id + "/" + cleanFilename);
			if (file.exists()) {
				file.delete();
				return ResponseEntity.ok("Image " + filename + " deleted");
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image " + filename + " not found");
			}
		}else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized to create service");
        }
    }
    */
}