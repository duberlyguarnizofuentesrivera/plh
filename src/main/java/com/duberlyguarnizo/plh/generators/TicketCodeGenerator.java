package com.duberlyguarnizo.plh.generators;

import com.duberlyguarnizo.plh.ticket.Ticket;
import com.duberlyguarnizo.plh.user.User;
import org.hibernate.Session;
import org.hibernate.tuple.AnnotationValueGeneration;
import org.hibernate.tuple.GenerationTiming;
import org.hibernate.tuple.ValueGenerator;

import java.time.LocalDateTime;
import java.util.Optional;

public class TicketCodeGenerator implements AnnotationValueGeneration<TicketCode> {

    CurrentUserAuditorAware userAuditorAware;
    private final ValueGenerator<String> generator = new ValueGenerator<>() {
        @Override
        public String generateValue(Session session, Object owner) {
            Ticket ticket = (Ticket) owner;
            StringBuilder result = new StringBuilder();
            int dayInt = LocalDateTime.now().getDayOfYear();
            Optional<User> currentUser = userAuditorAware.getCurrentAuditor();
            String dayString;
            if (dayInt < 10) {
                dayString = "00" + dayInt;
            } else {
                if (dayInt < 100) {
                    dayString = "0" + dayInt;
                } else {
                    dayString = String.valueOf(dayInt);
                }
            }
            //FORMAT example: DUB059-8547
            result.append(currentUser.map(user -> user.getUsername() //get first 3 chars in Uppercase
                                    .toUpperCase()
                                    .subSequence(0, 3))
                            .orElse("NUS")) //-> No USer, should be a very rare case
                    .append(dayString)
                    .append("-")
                    .append(ticket.getId());
            return result.toString();
        }
    };

    @Override
    public void initialize(TicketCode ticketCode, Class<?> aClass) {
        //empty...
    }

    @Override
    public GenerationTiming getGenerationTiming() {
        return GenerationTiming.INSERT;
    }

    @Override
    public ValueGenerator<?> getValueGenerator() {
        return generator;
    }

    @Override
    public boolean referenceColumnInSql() {
        return false;
    }

    @Override
    public String getDatabaseGeneratedReferencedColumnValue() {
        return null;
    }
}
