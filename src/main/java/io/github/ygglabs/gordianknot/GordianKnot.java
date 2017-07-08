package io.github.ygglabs.gordianknot;

import com.google.common.collect.ImmutableSet;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.context.ContextCalculator;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Plugin(
        id = "gordianknot",
        name = "GordianKnot",
        description = "Untying the impossible permissions setup, for the Living Labyrinth",
        authors = {
                "ryantheleach"
        }
)
public class GordianKnot {

    @Listener
    public void onChangeServiceProvider(ChangeServiceProviderEvent event) {
        if (event.getNewProvider() instanceof PermissionService) {
            ((PermissionService) event.getNewProvider()).registerContextCalculator(new CareerContextCalculator());
        }
    }

    static class CareerContextCalculator implements ContextCalculator<Subject> {

        public static Set<Context> rootContext = ImmutableSet.of();
        public static String contextType = "career".toLowerCase(Locale.ENGLISH);
        public static Context engineer = new Context(contextType, "engineer".toLowerCase(Locale.ENGLISH));
        public static Context explorer = new Context(contextType, "explorer".toLowerCase(Locale.ENGLISH));
        public static Set<Context> validContexts = ImmutableSet.of(engineer, explorer);

        private Context getContext(Subject subject) {
            Optional<String> currentCareer = subject.getOption(rootContext, contextType);
            if (currentCareer.isPresent()) {
                String career = currentCareer.get();
                boolean isEng = career.equals(engineer.getName());
                boolean isExp = career.equals(explorer.getName());
                if (isEng) {
                    return engineer;
                }
                if (isExp) {
                    return explorer;
                }
            }
            return null;
        }

        @Override public void accumulateContexts(Subject calculable, Set<Context> accumulator) {
            Context careerContext = getContext(calculable);
            if (careerContext != null) {
                accumulator.add(careerContext);
            }
        }

        @Override public boolean matches(Context context, Subject subject) {
            if (!validContexts.contains(context)) {
                return false;
            }
            return getContext(subject) == context;
        }
    }
}
