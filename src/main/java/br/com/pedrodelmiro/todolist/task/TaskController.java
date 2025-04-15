package br.com.pedrodelmiro.todolist.task;


import br.com.pedrodelmiro.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.config.Task;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request){
        System.out.println("Chegou no controller" + request.getAttribute("idUser"));
        var idUser = request.getAttribute("idUser");
        taskModel.setIdUser((UUID) idUser);

        var currentDate = LocalDateTime.now();
        if(taskModel.getEndAt().isBefore(taskModel.getStartAt())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de inicio deve ser maior que a data atual");

        }
        var task = this.taskRepository.save(taskModel);
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }

    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request){
        var idUser = request.getAttribute("idUser");
        var tasks = this.taskRepository.findByIdUser((UUID) idUser);
        return tasks;
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id){

            var idUser = request.getAttribute("idUser");
        var task = this.taskRepository.findById(id).orElse(null);

            if(task == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tarefa não encontrada");
            }


            if(!task.getIdUser().equals(idUser)){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("sem permissão");

            }
            Utils.copyNonNullProperties(taskModel, task);
            var taskUpdated = this.taskRepository.save(task);
            taskModel.setId(id);
            taskModel.setIdUser((UUID) idUser);

            return ResponseEntity.ok().body(this.taskRepository.save(taskUpdated));

    }
}
