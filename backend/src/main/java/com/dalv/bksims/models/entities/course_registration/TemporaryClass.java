package com.dalv.bksims.models.entities.course_registration;

import com.dalv.bksims.models.entities.user.Student;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "temporary_class")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TemporaryClass {
    @EmbeddedId
    private TemporaryClassId temporaryClassId;

    @ManyToOne
    @MapsId("proposed_class_id")
    @JoinColumn(name = "proposed_class_id")
    private ProposedClass proposedClass;

    @ManyToOne
    @MapsId("student_id")
    @JoinColumn(name = "student_id")
    private Student student;
}
