package software.jsq.ecs;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public final class ToSmithyPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getTasks().create("writeModel", ToSmithyTask.class);
    }
}
