package com.example.codersfree.controller.instructor;

import com.example.codersfree.service.CategoryService;
import com.example.codersfree.service.CourseService;
import com.example.codersfree.service.LevelService;
import com.example.codersfree.service.PriceService;
import com.example.codersfree.dto.CourseCreateDto;
import com.example.codersfree.dto.CourseUpdateDto;
import com.example.codersfree.dto.MessageDto;
import com.example.codersfree.model.Course;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/instructor")
public class CourseInstructorController {

    @Autowired
    private CourseService courseService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private LevelService levelService;
    @Autowired
    private PriceService priceService;

    @GetMapping
    public String index(Model model, Authentication authentication) {
        String email = authentication.getName();
        List<Course> courses = courseService.findByInstructorEmail(email);
        model.addAttribute("courses", courses);
        return "instructor/courses/index";
    }

    @GetMapping("/courses/create")
    public String create(Model model) {

        if (!model.containsAttribute("courseDto")) {
            model.addAttribute("courseDto", new CourseCreateDto());
        }

        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("levels", levelService.findAll());
        model.addAttribute("prices", priceService.findAll());

        return "instructor/courses/create";
    }

    @PostMapping("/courses/create")
    public String store(
            @Valid @ModelAttribute("courseDto") CourseCreateDto courseDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Authentication authentication) {

        // Verificar Errores de Validación
        if (bindingResult.hasErrors()) {
            
            redirectAttributes.addFlashAttribute(
                "org.springframework.validation.BindingResult.courseDto",
                bindingResult
            );

            redirectAttributes.addFlashAttribute("courseDto", courseDto);

            return "redirect:/instructor/courses/create"; 
        }

        String email = authentication.getName();
        Course newCourse = courseService.createCourse(courseDto, email);

        redirectAttributes.addFlashAttribute(
            "success", 
            "¡Curso creado! Ahora puedes añadir más detalles."
        );

        return "redirect:/instructor/courses/" + newCourse.getSlug() + "/edit";
    }

    @GetMapping("/courses/{slug}/edit")
    public String edit(
        @PathVariable String slug, 
        Model model, 
        RedirectAttributes redirectAttributes,
        Authentication authentication
    ) {

        Course course = courseService.findBySlugAndInstructorEmail(slug, authentication.getName());

        if (!model.containsAttribute("courseDto")) {            
            CourseUpdateDto courseDto = new CourseUpdateDto(course);
            model.addAttribute("courseDto", courseDto);
        }

        model.addAttribute("course", course);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("levels", levelService.findAll());
        model.addAttribute("prices", priceService.findAll());

        return "instructor/courses/edit";
    }

    @PostMapping("/courses/{slug}/edit")
    public String update(
        @PathVariable String slug,
        @Valid @ModelAttribute("courseDto") CourseUpdateDto courseDto,
        BindingResult bindingResult,
        @RequestParam(name = "file", required = false) MultipartFile file,
        RedirectAttributes redirectAttributes
    ) {

        if (bindingResult.hasErrors()) {

            redirectAttributes.addFlashAttribute(
                "org.springframework.validation.BindingResult.courseDto",
                bindingResult
            );

            redirectAttributes.addFlashAttribute("courseDto", courseDto);

            return "redirect:/instructor/courses/" + slug + "/edit";
        }

        try {

            courseService.updateCourse(slug, courseDto, file);

            redirectAttributes.addFlashAttribute(
                "success", 
                "Información del curso actualizada correctamente."
            );
            
            return "redirect:/instructor/courses/" + slug + "/edit";

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                "error", 
                e.getMessage()
            );
            
            return "redirect:/instructor/courses/" + slug + "/edit";
        }
    }

    @GetMapping("/courses/{slug}/video")
    public String getVideoPage(
        @PathVariable String slug, 
        Model model, 
        RedirectAttributes redirectAttributes,
        Authentication authentication
    ) 
    {

        Course course = courseService.findBySlugAndInstructorEmail(slug, authentication.getName());
        model.addAttribute("course", course);

        return "instructor/courses/video";

    }

    @PostMapping("/courses/{slug}/video")
    public String storeVideo(
        @PathVariable String slug,
        @RequestParam(name = "file") 
        MultipartFile file,
        RedirectAttributes redirectAttributes
    ) {

        try {

            courseService.updateCourseVideo(slug, file);
            redirectAttributes.addFlashAttribute(
                "success", 
                "Video promocional actualizado correctamente."
            );

            return "redirect:/instructor/courses/" + slug + "/video";

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                "error", 
                e.getMessage()
            );

            return "redirect:/instructor/courses/" + slug + "/video";
            
        }

    }

    @GetMapping("/courses/{slug}/goals")
    public String getGoalsPage(
        @PathVariable String slug, 
        Model model,
        Authentication authentication
    ) {

        Course course = courseService.findBySlugAndInstructorEmail(slug, authentication.getName());
        model.addAttribute("course", course);

        return "instructor/courses/goals";
    }

    @GetMapping("/courses/{slug}/requirements")
    public String getRequirementsPage(
            @PathVariable String slug,
            Model model,
            RedirectAttributes redirectAttributes,
            Authentication authentication
        ) {

        Course course = courseService.findBySlugAndInstructorEmail(slug, authentication.getName());
        model.addAttribute("course", course);

        return "instructor/courses/requirements";
    }

    @GetMapping("/courses/{slug}/messages")
    public String messages(
        @PathVariable String slug, 
        Model model,
        Authentication authentication
    ) {

        Course course = courseService.findBySlugAndInstructorEmail(slug, authentication.getName());

        if (!model.containsAttribute("messageDto")) {
            MessageDto dto = new MessageDto(
                course.getWelcomeMessage(),
                course.getGoodbyeMessage()
            );
            model.addAttribute("messageDto", dto);
        }

        model.addAttribute("course", course);

        return "instructor/courses/messages";
    }

    @PostMapping("/courses/{slug}/messages")
    public String postMessage(@PathVariable String slug,
            @Valid @ModelAttribute("messageDto") MessageDto messageDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        // Verificar Errores de Validación
        if (bindingResult.hasErrors()) {
            
            redirectAttributes.addFlashAttribute(
                "org.springframework.validation.BindingResult.messageDto",
                bindingResult
            );

            redirectAttributes.addFlashAttribute("messageDto", messageDto);

            return "redirect:/instructor/courses/" + slug + "/messages";
        }
      
        courseService.updateCourseMessage(slug, messageDto);
        
        redirectAttributes.addFlashAttribute("success", "Mensaje guardado correctamente.");
  
        return "redirect:/instructor/courses/" + slug + "/messages";

    }

}