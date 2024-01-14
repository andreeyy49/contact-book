package com.example.contactbook.listener;

import com.example.contactbook.Contact;
import com.example.contactbook.service.ContactService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataContactsCreator {
    private final ContactService contactService;

    /*@EventListener(ApplicationStartedEvent. class)*/
    public void creatTaskData(){
        log.debug("Calling DatabaseTaskCreator->creatTaskData...");

        List<Contact> contacts = new ArrayList<>();
        for(int i = 0; i < 9; i++){
            int value = i+1;
            Contact contact = new Contact();
            contact.setId((long)value);
            contact.setFirstName("FirstName " + value);
            contact.setLastName("LastName " + value);
            contact.setEmail("email" + value + "@gmail.com");
            contact.setPhone("+7959123456" + value);

            contacts.add(contact);
        }

        contactService.batchInsert(contacts);
    }
}
