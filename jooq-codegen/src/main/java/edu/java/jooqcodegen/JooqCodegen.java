package edu.java.jooqcodegen;

import org.jooq.codegen.GenerationTool;
import org.jooq.meta.jaxb.Configuration;
import org.jooq.meta.jaxb.Database;
import org.jooq.meta.jaxb.Generate;
import org.jooq.meta.jaxb.Generator;
import org.jooq.meta.jaxb.Jdbc;
import org.jooq.meta.jaxb.Target;

@SuppressWarnings({"MultipleStringLiterals", "UncommentedMain"})
public class JooqCodegen {
    private JooqCodegen() {
        throw new IllegalStateException();
    }

    public static void main(String[] args) throws Exception {
        Generate options = new Generate()
            .withGeneratedAnnotation(true)
            .withGeneratedAnnotationDate(false)
            .withNullableAnnotation(true)
            .withNullableAnnotationType("org.jetbrains.annotations.Nullable")
            .withNonnullAnnotation(true)
            .withNonnullAnnotationType("org.jetbrains.annotations.NotNull")
            .withJpaAnnotations(false)
            .withValidationAnnotations(true)
            .withSpringAnnotations(true)
            .withConstructorPropertiesAnnotation(true)
            .withConstructorPropertiesAnnotationOnPojos(true)
            .withConstructorPropertiesAnnotationOnRecords(true)
            .withFluentSetters(false)
            .withDaos(false)
            .withPojos(true);

        Configuration configuration = new Configuration()
            .withJdbc(new Jdbc()
                .withDriver("org.postgresql.Driver")
                .withUrl("jdbc:postgresql://localhost:5432/scrapper")
                .withUser("postgres")
                .withPassword("postgres"))
            .withGenerator(new Generator()
                .withDatabase(new Database()
                    .withName("org.jooq.meta.postgres.PostgresDatabase")
                    .withIncludes(".*")
                    .withExcludes("")
                    .withInputSchema("public")
                )
                .withTarget(new Target()
                    .withPackageName("edu.java.scrapper.domain.jooq")
                    .withDirectory("scrapper/src/main/java"))
                .withGenerate(options));

        GenerationTool.generate(configuration);
    }
}
