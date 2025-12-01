package com.example.codersfree.controller;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.codersfree.model.Course;
import com.example.codersfree.model.Lesson;
import com.example.codersfree.model.Module;
import com.example.codersfree.model.User;
import com.example.codersfree.service.CartService;
import com.example.codersfree.service.CategoryService;
import com.example.codersfree.service.CourseService;
import com.example.codersfree.service.LevelService;
import com.example.codersfree.service.PriceService;
import com.example.codersfree.service.UserService;

@Controller
@RequestMapping("/courses")
public class CourseController {
    
    @Autowired private CourseService courseService;
    @Autowired private CategoryService categoryService;
    @Autowired private LevelService levelService;
    @Autowired private PriceService priceService;
    @Autowired private CartService cartService;
    @Autowired private UserService userService;

    @GetMapping
    public String index(@RequestParam(value = "search", required = false) String searchTerm, @RequestParam(value = "categories", required = false) List<Long> categoryIds, @RequestParam(value = "levels", required = false) List<Long> levelIds, @RequestParam(value = "prices", required = false) List<Long> priceIds, @RequestParam(value = "sort", required = false, defaultValue = "createdAt_desc") String sortParam, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "8") int size, Model model) {
        Sort sort = switch (sortParam) { case "createdAt_asc" -> Sort.by(Sort.Direction.ASC, "createdAt"); case "name_asc" -> Sort.by(Sort.Direction.ASC, "name"); case "name_desc" -> Sort.by(Sort.Direction.DESC, "name"); default -> Sort.by(Sort.Direction.DESC, "createdAt"); };
        Pageable pageable = PageRequest.of(page, size, sort);
        model.addAttribute("courses", courseService.searchAndFilterCourses(searchTerm, categoryIds, levelIds, priceIds, pageable));
        model.addAttribute("allCategories", categoryService.findAll());
        model.addAttribute("allLevels", levelService.findAll());
        model.addAttribute("allPrices", priceService.findAll());
        model.addAttribute("selectedSearchTerm", searchTerm);
        model.addAttribute("selectedCategories", categoryIds != null ? categoryIds : List.of());
        model.addAttribute("selectedLevels", levelIds != null ? levelIds : List.of());
        model.addAttribute("selectedPrices", priceIds != null ? priceIds : List.of());
        model.addAttribute("selectedSort", sortParam);
        return "courses/index";
    }

    @GetMapping("/{slug}")
    public String show(@PathVariable String slug, Model model, Principal principal) {
        Course course = courseService.findBySlug(slug);
        model.addAttribute("course", course);
        model.addAttribute("isInCart", cartService.contains(course.getId()));
        boolean isEnrolled = false;
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            // --- CAMBIO: USAR HELPER ---
            isEnrolled = user.isEnrolled(course.getId());
        }
        model.addAttribute("isEnrolled", isEnrolled);
        return "courses/show";
    }
    
    @GetMapping("/my-courses")
    public String myCourses(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        List<Course> enrolledCourses = courseService.findEnrolledCourses(user.getId());
        model.addAttribute("courses", enrolledCourses);
        return "courses/my-courses";
    }

    @GetMapping("/{slug}/learn")
    public String learn(@PathVariable String slug, Principal principal) {
        Course course = courseService.findBySlug(slug);
        User user = userService.findByEmail(principal.getName());
        if (!user.isEnrolled(course.getId())) return "redirect:/courses/" + slug;
        Lesson lessonToPlay = courseService.findFirstIncompleteLesson(course, user);
        if (lessonToPlay == null) return "redirect:/courses/" + slug;
        return "redirect:/courses/" + slug + "/learn/" + lessonToPlay.getId();
    }

    @GetMapping("/{slug}/learn/{lessonId}")
    public String learnLesson(@PathVariable String slug, @PathVariable Long lessonId, Model model, Principal principal) {
        Course course = courseService.findBySlug(slug);
        User user = userService.findByEmail(principal.getName());
        if (!user.isEnrolled(course.getId())) return "redirect:/courses/" + slug;
        Lesson currentLesson = course.getModules().stream().flatMap(m -> m.getLessons().stream()).filter(l -> l.getId().equals(lessonId)).findFirst().orElseThrow(() -> new IllegalArgumentException("Lección no encontrada"));
        int progress = courseService.calculateProgress(course, user);
        List<Lesson> allLessons = course.getModules().stream().sorted(Comparator.comparing(Module::getId)).flatMap(m -> m.getLessons().stream().sorted(Comparator.comparing(Lesson::getPosition))).collect(Collectors.toList());
        int currentIndex = -1;
        for (int i = 0; i < allLessons.size(); i++) { if (allLessons.get(i).getId().equals(currentLesson.getId())) { currentIndex = i; break; } }
        Lesson previous = (currentIndex > 0) ? allLessons.get(currentIndex - 1) : null;
        Lesson next = (currentIndex < allLessons.size() - 1) ? allLessons.get(currentIndex + 1) : null;
        model.addAttribute("course", course);
        model.addAttribute("currentLesson", currentLesson);
        model.addAttribute("previous", previous);
        model.addAttribute("next", next);
        model.addAttribute("progress", progress);
        model.addAttribute("user", user);
        return "courses/learn";
    }

    @PostMapping("/{slug}/learn/{lessonId}/toggle")
    public String toggleLesson(@PathVariable String slug, @PathVariable Long lessonId, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        Course course = courseService.findBySlug(slug);
        Lesson lesson = course.getModules().stream().flatMap(m -> m.getLessons().stream()).filter(l -> l.getId().equals(lessonId)).findFirst().orElse(null);
        if (lesson != null) {
            courseService.toggleLessonCompletion(user, lesson);
            userService.save(user);
        }
        return "redirect:/courses/" + slug + "/learn/" + lessonId;
    }
}