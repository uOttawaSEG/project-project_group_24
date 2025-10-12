public class Course
{
    static coursecount;
    Student[] slots;
    final long id = coursecount;
    final Tutor tutor;
    int filledslots;
    public Course()
    {
        coursecount++;
        slots = Students[5];
    }
    public Course(int slotcap)
    {
        coursecount++;
        slots = Students[slotcap];
    }
    public void fillSlot(Student s)
    {
        if(filledslots<slots.length())
        {
            slot[filledslots] = s;
            filledslots++;
        }
    }
}