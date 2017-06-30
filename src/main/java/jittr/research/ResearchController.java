package jittr.research;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jittr.account.JitterService;
import jittr.services.DomainObjValidationError;
import jittr.support.web.MessageHelper;

import java.security.Principal;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

@Controller
@RequestMapping("/research")
public class ResearchController{
    
    private final static String RESEARCH_REDIRECT_SUCCESS_CREATION_URL = "redirect:/research/"; 
    
    private final static String RESEARCH_NEW_PATH = "/new";
    
    private final static String RESEARCH_VIEW_NAME = "task-ld/";
    private final static String RESEARCH_NEW_VIEW_NAME = "task-ld/new";
    
    private final static String CURRENT_RESEARCH = "currentResearch";
    private final static String SUBJECT_FORM = "currentResearch";
    
    private final static String JITTER_RESEARCHES = "jitterResearches";
    private final static String FREE_RESEARCHES = "subjForm";
    
    @Value("${jittr.repository-rest.app-name}")
    private String defaultJitter;
    
    private ResearchService researchService;
    
    private JitterService jitterService;

    @Autowired
    ResearchController(ResearchService researchService, JitterService jitterService) {
        this.researchService = researchService; 
        this.jitterService = jitterService;
    }

    /**
     * Retrieves research with given id, provided via path variable.
     * 
     * @param principal {@link Principal} of the current {@link JitterDto}. If null
     * @param id unique id of {@link IJudgeResearch} to be found. If null default research
     *           will be found.
     * @param model {@link Model} to be filled with data.
     * @return name of view for displaying.
     * 
     * @throws IllegalArgumentException if model in {@code null}.
     */
    @ResponseStatus(HttpStatus.FOUND)
    @GetMapping(value = {"/{id}", ""})
    public String findOne(final Principal principal, 
            @PathVariable final Optional<Long> id, final Model model) {
        IJudgeResearch currentResearch = id.map(i -> researchService.findOne(i.longValue()))
                .orElseGet(() -> researchService.findOneDefault());
        model.addAttribute(CURRENT_RESEARCH, currentResearch);
        this.fillLeftMenuData(principal, model);
        return RESEARCH_VIEW_NAME + currentResearch.getStateAsString().toLowerCase();
    }
    
    /**
     * Show form for creating new {@link IJuudgeResearch}.
     * 
     * @param principal current Jitter's {@link Principal}.
     * @param model
     * @return
     */
    @GetMapping(value = {RESEARCH_NEW_PATH})
    public String createNew(final Principal principal, final Model model) {
        model.addAttribute(CURRENT_RESEARCH, new JudgeResearchBrandDto("New Research"));
        model.addAttribute(SUBJECT_FORM, new JudgeResearchBrandForm());
        this.fillLeftMenuData(principal, model);
        return RESEARCH_NEW_VIEW_NAME;
    }
    
    /**
     * Create and persists new {@link IJudgeResearch} or returns form again 
     * if validation errors occurred.
     * 
     * @param subjForm form with research subject.
     * @param errors holds validation errors.
     * @param ra
     * @param model
     * @param principal current Jitter's {@link Principal}.
     * @return
     * @throws DomainObjValidationError if validation errors occurred during persisiting.
     */
    @PostMapping(value = {RESEARCH_NEW_PATH})
    public String createNew(@Valid @ModelAttribute(SUBJECT_FORM) final JudgeResearchBrandForm subjForm,
            final Errors errors, RedirectAttributes ra, final Model model, final Principal principal) 
                    throws DomainObjValidationError {
        if (errors.hasErrors()) {
            model.addAttribute(CURRENT_RESEARCH, new JudgeResearchBrandDto("New Research"));
            return RESEARCH_NEW_VIEW_NAME;
        }
        IJudgeResearch newResearch = new JudgeResearchLearningDto(subjForm.getName());
        newResearch.setJitter(jitterService.getJitterFromAuth());
        IJudgeResearch saved = researchService.save(newResearch);
        MessageHelper.addSuccessAttribute(ra, "research.create.success");    
        
        return RESEARCH_REDIRECT_SUCCESS_CREATION_URL + saved.getId();
    }
    
    /*
     * Populate model with date for left menu.
     */
    private void fillLeftMenuData(final Principal principal, final Model model) {
        Collection<IJudgeResearch> researches = null;
        
        if (principal != null) { 
            researches = researchService.findAll(principal.getName());
            model.addAttribute(JITTER_RESEARCHES,
                    this.getJitterResearches(researches,  principal.getName()));
        } else {
            researches = researchService.findAllDefault();           
        }
        model.addAttribute(FREE_RESEARCHES, this.getFreeResearches(researches));
    }
    
    /*
     * Extracts from all accessible researches free researches.
     */
    private Collection<IJudgeResearch> 
        getFreeResearches(final Collection<IJudgeResearch> researches) {
        return researches.stream()
                  .filter(research -> 
                      research.getJitter().getUsername().equalsIgnoreCase(defaultJitter))
                  .collect(Collectors.toList());
    }
    
    /*
     * Extracts from all accessible researches personal researches.
     */
    private Collection<IJudgeResearch> getJitterResearches(final Collection<IJudgeResearch> researches,
            final String currentUsername) {
        return researches.stream()
                  .filter(research -> 
                      research.getJitter().getUsername().equalsIgnoreCase(currentUsername))
                  .collect(Collectors.toList());
    }
    
}
