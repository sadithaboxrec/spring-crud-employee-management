import { useState } from "react";
import "./PostUser.css";
import Form from 'react-bootstrap/Form';
import Button from 'react-bootstrap/Button';
import { useNavigate } from "react-router-dom";


const PostUser = ()=>{

    const [formData,setFormData ] = useState ({
        name:"",
        email:"",
        phone:"",
        department:""
    })


    const handleInputChange = (event)=>{

        const {name,value}=event.target;

        setFormData({
            ...formData,
            [name]:value,
        })
    };

    const navigate= useNavigate();

    const handleSubmit = async(e) =>{
        e.preventDefault();

        console.log(formData);

        try{
            const response = await fetch("http://localhost:8080/api/employee", {
                method:"post",
                headers: {"Content-type":"application/json"},
                body:JSON.stringify(formData),

            });

            const data =await response.json();
            console.log("Employees Created",data);
            navigate("/");

        } catch(error){
            console.log("Error creating employees: ",error.message);
        }

    }


    return(
        <>


       <div className="center-form">
            <h1>Add New Employee</h1>

            <Form onSubmit={handleSubmit}>
                    <Form.Group controlId="FormBasicName">
                        <Form.Control type="text" name="name" value={formData.name} onChange={handleInputChange} placeholder="Name"/>
                    </Form.Group>

                    <Form.Group controlId="FormBasicEmail">
                        <Form.Control type="email" name="email" value={formData.email} onChange={handleInputChange}
                        placeholder="Email" />
                    </Form.Group>

                    <Form.Group controlId="FormBasicPhone">
                        <Form.Control type="text" name="phone" value={formData.phone} onChange={handleInputChange} 
                        placeholder="Phone"/>
                    </Form.Group>

                    <Form.Group controlId="FormBasicDepartment">
                        <Form.Control type="text" name="department" value={formData.department} onChange={handleInputChange} 
                        placeholder="Department"/>
                    </Form.Group>

                    <Button variant="primary" type="submit" className="w-100">Add Employee</Button>
                </Form>

        </div> 




        </>
    )

}

export default PostUser;