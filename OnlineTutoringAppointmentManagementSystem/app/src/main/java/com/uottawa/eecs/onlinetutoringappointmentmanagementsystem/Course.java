public class Course
{
    static int coursecount;
    Student[] slots;
    final long id = coursecount;
    final Tutor tutor;
    int filledslots;
    public Course(Tutor t)
    {
        coursecount++;
        slots = new Student[5];
        tutor = t;
    }
    public Course(Tutor t,int slotcap)
    {
        coursecount++;
        slots = new Student[slotcap];
        tutor = t;
    }
    public void fillSlot(Student s)
    {
        if(filledslots<slots.length)
        {
            slots[filledslots] = s;
            filledslots++;
        }
    }
}