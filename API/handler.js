const { nanoid } = require('nanoid');
const user = [];

const register = (request, h) => {
    const {
        name, email, password
    } = request.payload;
    const emailIndex = user.findIndex((users) => users.email === email);

    if (emailIndex !== -1) {
        const response = h.response({
            "error": "true",
            "message": "User not Created. Email used."
        });
        response.code(400);
        return response;
    }
    const id = nanoid();
    const newUser = {
        id,
        name,
        email,
        password
    };
    user.push(newUser);
    const response = h.response({
        "error": "false",
        "message": "User Created"
    });
    response.code(200);
    return response;
}

const login = (request, h) => {
    const {
        email, password
    } = request.payload;

    const userAccount = user.filter((users) => users.email == email)[0];

    if (userAccount === undefined || userAccount.password !== password){
        const response = h.response({
            "error": "true",
            "message": "Email not found or Wrong password"
        });
        response.code(400);
        return response;
    } else {
        userAccount.token = "tukkantuk";
        const response = h.response({
            "error": "false",
            "message": "success",
            "loginResult": userAccount.map((users) => ({
                userId : users.id,
                name : users.name,
                "token" : "tukkantuk"
            }))
        });
        response.code(200);
        return response;
    }
}

const stories = (request, h) => {
    const {
        description, photo, lat, lon
    } = request.payload;
}

module.exports = {
    register,
    login,
    stories
}