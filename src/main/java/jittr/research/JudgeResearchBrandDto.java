package jittr.research;

public class JudgeResearchBrandDto extends JudgeResearchDto {

    public JudgeResearchBrandDto(String name) {
        super(name, State.BRANDNAME);
    }
    
    public JudgeResearchBrandDto() {
        this(null);
    }


    @Override
    public JudgeResearchDto getNextState() {
        return new JudgeResearchLearningDto(name);
    }



}
