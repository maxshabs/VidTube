import './Search.css';

import { useRef } from "react";

function Search({ doSearch }) {

    const searchBox = useRef(null);

    const search = (function () {
        doSearch(searchBox.current.value);
    });

    return (
        <div className="input-group" id="search-box">
            <input ref={searchBox} onKeyUp={search} type="text" className="form-control" placeholder="Search" aria-label="Search" aria-describedby="button-addon2"></input>
            <button className="btn btn-outline-secondary" type="button" id="button-addon2"><i className="bi bi-search"></i></button>
        </div>
    );
}

export default Search;